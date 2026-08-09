# ExoPet Cloud — 面试问题与知识点整理

> 记录开发过程中遇到的问题、解决方案以及面试可能问到的知识点。
> 按面试考点分类，方便复习。

---

## 一、项目整体

### 1. 项目是做什么的？
垂直异宠领域的一站式医疗服务平台：**AI问诊 → 真人兽医 → 急诊视频 → 健康管理** 闭环。

### 2. 为什么用微服务？
按业务领域拆为 7 个独立服务（auth/user/consult/ai/pet/hospital/notification），服务挂了不影响其他服务，独立部署独立扩缩容。

### 3. 有多少张表？数据库设计策略？
16 张表，单库（exopet）+ 按 module 分包隔离。不拆分库的原因是项目规模小，单库足以支撑，避免分布式事务的复杂度。

---

## 二、Nacos（注册中心）

### 4. Nacos 是什么？为什么用它？
**Nacos = 动态通讯录**。每个服务启动时向 Nacos 注册自己的地址，Gateway 转发请求时动态查询，而不是写死地址。

**没有 Nacos 时：**
```properties
# 写死地址，换端口/加实例都要改配置
spring.cloud.gateway.routes[0].uri=http://localhost:9200
```

**有 Nacos 时：**
```properties
# 动态查询，服务挂了自动规避，加实例自动负载均衡
spring.cloud.gateway.routes[0].uri=lb://exopet-auth
```

### 5. 为什么 exclude NacosConfigAutoConfiguration？
`spring-ai-alibaba-starter` 额外引入了 Nacos Config（配置中心）的自动配置，它会尝试连接 Nacos 配置中心。但项目只用了 Nacos 的服务发现功能，不需要配置管理，不排除就会启动失败：

```java
@SpringBootApplication(exclude = {NacosConfigAutoConfiguration.class})
```

---

## 三、Gateway（网关）

### 6. Gateway 做了什么？
- **路由转发**：根据路径前缀分发到对应微服务
- **统一鉴权**：拦截所有请求，从 Header 取 Token 校验
- **CORS 跨域**：统一处理跨域，业务服务不需要再配

### 7. 为什么需要 Gateway？没有不行吗？
没有 Gateway，前端要配 6 个端口，每个微服务都要自己写鉴权（6 遍重复代码），跨域也要配 6 遍。

Gateway = 统一入口，前端只认一个地址（:8080），后端换端口/加服务前端无感知。

### 8. Gateway 503 怎么排查的？
直连 auth（:9200）通，走 Gateway（:8080）就 503。

原因是 `lb://` 路由需要 **Spring Cloud LoadBalancer** 依赖：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### 9. Gateway 启动报错 "Spring MVC incompatible with Gateway"？
Gateway 基于 **WebFlux**（响应式，Netty），`spring-boot-starter-web` 引入的是 **Spring MVC**（同步阻塞，Tomcat），两者冲突。

解决：在 Gateway 模块排除 common 里的 web：
```xml
<exclusion>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</exclusion>
```

| 特性 | Spring MVC | Spring WebFlux |
|------|-----------|---------------|
| I/O 模型 | 同步阻塞 | 非阻塞 |
| 容器 | Tomcat | Netty |
| 高并发 | 线程池扩容 | 事件驱动 |
| 适用 | CRUD | **网关、高并发** |

---

## 四、JWT 鉴权

### 10. JWT 是什么？为什么用它？
JWT = JSON Web Token，一种自包含的 Token 格式。服务端不需要存 Session，客户端持有 JWT 即可完成身份认证。

结构：`Header.Payload.Signature`

### 11. 为什么 Token 要存 Redis，而不是只用 JWT？

| 场景 | 只用 JWT | JWT + Redis |
|------|---------|-------------|
| 踢人下线 | ❌ 没法踢 | ✅ 删 Redis 记录 |
| Redis 挂了 | — | ✅ 降级 JWT 本地验签 |
| 鉴权速度 | ✅ 快 | ⚠️ 多一次网络请求 |

**Gateway 的双保险逻辑：**
```java
// 1. 优先 Redis（支持踢人，响应快）
String userId = redisTemplate.get("exopet:token:" + token);
if (userId != null) return userId;

// 2. Redis 不可用 → JWT 本地验签（服务不中断）
Claims claims = Jwts.parser().verifyWith(signingKey).build()
                  .parseSignedClaims(token).getPayload();
```

### 12. 为什么不用 Spring Security？
场景简单——手机号+验证码登录，JWT 做 Token，Gateway 统一校验。Spring Security 的 UserDetails、PasswordEncoder 反而多余。

> **面试回答：** "用了 Gateway 做统一鉴权，没有在业务层再用 Spring Security。因为我们的场景比较简单，Spring Security 那一套对我们反而多余，就没引。"

### 13. "Using generated security password" 怎么解决的？
Nacos 依赖带入了 Spring Security，它自动生成了密码保护了所有接口。在启动类排除：
```java
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
```

---

## 五、Redis

### 14. Redis 在项目里怎么用的？

| 场景 | Key 模式 | TTL |
|------|---------|-----|
| **Token 缓存** | `exopet:token:{token}` → userId | 7 天 |
| **医生缓存** | `exopet:doctor:{id}` → 医生信息 | 30 分钟 |
| **病例缓存** | `exopet:pet:case:{id}` → 病例 | 1 小时 |

实际只实现了 Token 缓存，医生和病例缓存规划了但还没做（演示项目数据库本地，直接查库也很快）。

---

## 六、消息队列（RocketMQ vs Kafka）

### 15. 为什么选 RocketMQ 而不是 Kafka？

| 维度 | Kafka | RocketMQ |
|------|-------|----------|
| Spring Cloud Alibaba 集成 | ⚠️ 需手动 | ✅ 原生支持 |
| 消息追踪 | ❌ 需额外配 | ✅ 自带 |
| 控制台 | 需额外配 | ✅ 自带 |
| 吞吐 | 百万级/s（溢出） | 十万级/s（溢出） |
| 技术栈匹配 | 通用 | **阿里生态亲儿子** |

**核心原因：**
1. **轻量** — 每天几十条消息，Kafka 性能严重溢出
2. **适配阿里栈** — 已用 Nacos + Sentinel，RocketMQ 是生态原生的
3. **自带追踪** — 排错方便

### 16. 概念对比

```text
┌──────────┬────────────────┬────────────────┐
│   概念   │     Kafka      │    RocketMQ    │
├──────────┼────────────────┼────────────────┤
│ 消息分类 │ Topic          │ Topic          │
│ 分组消费 │ Consumer Group │ Consumer Group │
│ 分区并行 │ Partition      │ Message Queue  │
│ 消息偏移 │ Offset         │ Consume Queue  │
│ 可靠性   │ ACK            │ ACK            │
└──────────┴────────────────┴────────────────┘
```

核心概念完全相通，会了一个另一个上手很快。

---

## 七、Sentinel

### 17. Sentinel 三大功能是什么？

| 功能 | 说明 | 类比 |
|------|------|------|
| **流量控制** | QPS 限流、并发线程数限流，支持冷启动、匀速排队 | 地铁闸机限流 |
| **熔断降级** | 资源不稳定（响应慢、异常多）时快速失败 | 电路跳闸 |
| **系统保护** | 自适应保护，入口流量与系统负载平衡 | 水库泄洪 |

### 18. 项目里哪些接口用了 Sentinel？
AI 三个接口（diagnose/mood-analysis/breed-recognize）都加了 Sentinel，限流时返回"AI服务繁忙"：

```java
@SentinelResource(
    value = "aiDiagnose",
    blockHandler = "diagnoseBlockHandler",  // 限流时
    fallback = "diagnoseFallback"           // 异常时
)
```

---

## 八、AI 服务（exopet-ai）

### 19. Spring AI 的 ChatClient 是什么？
Spring AI 的核心 API，类似 RestTemplate 调 HTTP——它是调大模型的统一客户端：
```java
String response = chatClient.prompt()
        .messages(new UserMessage(prompt, List.of(media)))
        .call()
        .content();
```

### 20. 为什么不用两个模型（文本+视觉分别用不同提供商）？
试过阿里云 DashScope（文本）+ 硅基流动（视觉）的方案，但 Spring AI 两个 starter 同时存在会导致 Bean 冲突。最终的方案是**全走硅基流动**，一个视觉模型处理所有请求（文字和图片都能处理）。

### 21. AI 返回的 JSON 怎么保证能存进 MySQL？
MySQL JSON 类型列要求值必须是合法 JSON。AI 返回可能有 markdown 代码块标记或格式错误。
```java
private String cleanJsonResponse(String raw) {
    // 1. 去掉 ```json ... ``` 标记
    // 2. 用 Jackson 验证是否合法 JSON
    // 3. 不合法就包一层 {"raw":"..."}
}
```

### 22. 对话式 AI 怎么设计的？

```
第一轮：用户描述 → AI 回复 + 追问 1-2 个问题（isDone: false）
第二轮：用户回答 → AI 深入分析 + 继续追问（isDone: false）
最终轮：结论 + 养护方案（isDone: true）

前端把历史对话以 [{user, ai}, ...] 形式传回来，
Service 拼到 Prompt 里实现上下文。
```

### 23. 阿里云欠费了怎么处理的？
`qwen-plus` 免费额度用完后报 `Arrearage`。换到硅基流动，注册送 19 元余额，用 `Qwen3-VL-8B-Instruct`，支持图文，速度也快。

### 24. 为什么模型响应慢？
用 27B 参数模型时响应要 45 秒。换成 8B 的 VL 模型后降到几秒。**模型大小直接影响响应速度。**

---

## 九、CORS 跨域

### 25. 为什么用 Config 包注册 CorsFilter？
直接注册 CorsFilter，优先级比 Spring Security/拦截器都高。请求还没到业务层跨域头就已经写回响应了——哪怕抛出 401/500，浏览器也能收到跨域头。

### 26. 业务服务还需要配 CORS 吗？
**走 Gateway 后不需要了。** 改前：浏览器直连各服务 → 每个都要配。改后：浏览器→Gateway→转发→只有 Gateway 配就行。

---

## 十、MySQL / 数据库

### 27. 为什么用自增 ID（AUTO）不用雪花 ID？
**单库单表不需要雪花 ID。** 雪花 ID 解决的是分布式中多个数据库节点同时写入不冲突的问题，你的项目就一个 MySQL，自增 ID 更直观。

### 28. 为什么 JSON 类型列要特殊处理？
`symptoms`、`ai_raw_response` 等列是 JSON 类型，存普通字符串会报 `Invalid JSON text`。需要确保存入的值是合法 JSON。

---

## 十一、其他

### 29. 项目启动顺序？
```bash
1. docker compose up -d nacos redis     # 中间件
2. mvn spring-boot:run -pl exopet-auth   # 逐个启动服务
3. mvn spring-boot:run -pl exopet-gateway  # 最后启动网关
4. cd ../exopet-mobile && npm run dev    # 前端
```

### 30. 耗时最长的 Bug 是什么？
AI 模型接入反复切换。从阿里云 DashScope → 硅基流动 → 两个共存 → 切回阿里 → 切回硅基。核心教训：**选定一个稳定的提供商，别反复横跳。**

### 31. WebSocket 推送通知怎么实现的？
前端连接 `ws://localhost:9209/ws/notification?userId=1`，后端维护 `userId → WebSocketSession` 的 Map。RocketMQ 消费者收到消息后，入库并调用 `webSocketHandler.sendToUser(userId, notification)` 实时推送。
- 断线自动清理 Session
- 消息格式与 REST API 返回的 Notification 结构一致

### 32. 三个 RocketMQ 消费者有什么区别？
实际上**代码逻辑完全一样**（收消息→入库→推送），只是监听的 topic 不同：
- `notification-push` → 通用系统通知
- `consult-remind` → 问诊提醒（type=CONSULT）
- `pet-health-remind` → 健康提醒（type=HEALTH）

区分 topic 的好处：后续可以给不同通知设置不同的优先级、重试策略、延迟级别。

---

## 十二、面试话术速查

| 面试官问 | 回答要点 |
|---------|---------|
| 为什么用微服务？ | 独立部署、独立扩缩容、故障隔离 |
| 跨服务调用怎么做？ | OpenFeign（同步） + RocketMQ（异步） |
| 分布式事务？ | 核心用 Seata AT，非核心用 RocketMQ 事务消息 |
| 缓存策略？ | Redis 缓存 Token，Gateway 优先查 Redis，不可用降级 JWT |
| AI 怎么做？ | Spring AI 封装，结构化 Prompt 输出 JSON |
| 网关鉴权怎么做的？ | Gateway 拦截，Redis 校验 → JWT 本地验签降级 |
| 实时通讯怎么实现的？ | WebSocket 即时推送消息，模拟医生自动回复 |
| 为什么不用 Spring Security？ | Gateway 统一鉴权已够，Spring Security 多余 |
| RocketMQ 为什么不用 Kafka？ | 项目负载低，RocketMQ 更轻量，阿里生态集成更好 |
| 图片识别怎么实现的？ | 视觉模型 `Qwen3-VL-8B-Instruct`，图片+文字一起分析 |

---

## 十三、面试官向 — 技术选型深度解析

### 1. 为什么选 Spring Cloud Alibaba 而不是 Netflix？

Netflix 组件大部分已停更（Eureka 2.0 烂尾），Alibaba 全家桶集成度更高：Nacos（注册+配置二合一）替代 Eureka + Config，Sentinel 替代 Hystrix，RocketMQ 替代 Kafka。一套依赖全搞定，版本兼容性问题少，中文文档和社区更友好。

### 2. Gateway 为什么做 Redis + JWT 双保险？

两级降级策略：优先查 Redis（支持踢人下线，响应快<1ms）→ Redis 挂了降级 JWT 本地验签（服务不中断）。核心思想：**基础设施故障不能让业务中断**。

### 3. 为什么选 RocketMQ 而不是 Kafka？

场景匹配度问题。Kafka 百万级/s 吞吐适合日志收集，项目每天几十到几百条消息，Kafka 性能溢出且要维护 Zookeeper。RocketMQ 自带控制台、消息轨迹、事务消息，阿里生态原生集成。**选技术不是选最火的，是选最合适的。**

### 4. 为什么用 Spring AI 而不是直接调大模型 API？

三个好处：① 模型切换零成本，从阿里云 DashScope 换硅基流动只改配置，业务代码不动；② 结构化输出，强制模型按 Schema 返回 JSON；③ ChatClient 统一抽象，不管背后是 GPT 还是通义千问，调用方式一样。

### 5. 数据库为什么单库不分库？

微服务是逻辑架构，分库是物理部署，两者不是强绑定。16 张表百万级数据，单库 MySQL 完全扛得住；不分库就不需要分布式事务，开发复杂度大幅降低；跨表 JOIN 直接写 SQL。表已按 `module_` 前缀分包，真要拆就是改个数据源配置的事。**不要为了微服务而微服务，不要为了分库而分库。**

### 6. WebSocket 为什么不用 STOMP？

STOMP 需要额外的消息代理层（RabbitMQ），项目只需要问诊聊天双向通讯 + 通知单向推送，原生 WebSocket 足够：ConcurrentHashMap 维护 userId→Session 映射，消息直接入库。**简单场景用简单方案，引入中间件是要付出维护成本的。**

### 7. AI 为什么做成多轮对话而不是一次出结论？

用户初始描述往往不完整，一次出结论偏差大。多轮对话让 AI 先追问关键信息（温度？排便？精神状态？），信息足够再出结论。好处：结论更准确 + 给用户"在看真医生"的感受。

### 8. 还有什么可以优化的方向？

| 优化点 | 方案 | 优先级 |
|--------|------|--------|
| 分布式事务 | Seata AT 保证问诊+支付最终一致性 | 高 |
| 视频问诊 | 声网 Agora（音视频不走服务器） | 中 |
| 监控告警 | Spring Boot Admin（JVM/线程/日志级别） | 低 |
| 全局限流 | Sentinel 覆盖所有接口防刷 | 中 |
| CI/CD | Docker + K8s 自动化部署 | 低 |
| 缓存扩展 | Redis 缓存医生信息、病例数据 | 中 |
| 通知渠道 | 加短信/邮件/微信模板消息 | 低 |

### 9. 面试时这些"坑"怎么说

| 实际情况 | 面试话术 |
|---------|---------|
| 忘了加 loadbalancer 依赖导致 503 | "在做网关路由时发现 `lb://` 需要负载均衡组件，排查后加上 LoadBalancer" |
| 阿里云欠费换模型 | "最初接入了阿里云和硅基流动双模型，但俩 starter 有 Bean 冲突，最终统一走硅基流动，简化架构" |
| 排除 SecurityAutoConfiguration | "Nacos 依赖带入了 Spring Security 自动配置，生成了默认密码拦截了接口，排查后排除即可" |
| Kafka 换 RocketMQ | "初期调研了 Kafka，但评估下来性能溢出且维护成本高，RocketMQ 更匹配阿里技术栈" |

**核心原则：** 每个"坑"的结尾都要落在"我发现了问题、分析了原因、给出了方案"，而不是"出了个 bug 我修好了"。
