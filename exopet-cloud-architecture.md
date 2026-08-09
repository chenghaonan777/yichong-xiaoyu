# ExoPet Cloud — 异宠小愈 后端微服务架构设计

> 本文档为 ExoPet（异宠小愈）后端微服务架构的整体设计说明
> 技术栈：Spring Cloud Alibaba + RocketMQ + Redis + Spring AI + MySQL

---

## 一、项目定位

垂直异宠领域的一站式医疗服务平台，覆盖：
- **AI问诊** → **真人兽医** → **急诊视频** → **健康管理** 闭环

---

## 二、技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.x | 基础框架 |
| Spring Cloud Alibaba | 2023.x | 微服务治理 |
| Spring Cloud Gateway | - | API网关（路由/鉴权/限流） |
| Nacos | 2.x+ | 注册中心 + 配置中心 |
| Sentinel | - | 流量控制 / 熔断降级 |
| Spring Security + JWT | - | 认证授权 |
| MyBatis-Plus | - | ORM |
| MySQL | 8.0+ | 业务数据库（单库多模块） |
| Redis | 7.x | Token缓存 / 病例缓存 / 分布式锁 |
| **RocketMQ** | - | **消息队列（异步解耦 / 削峰，阿里生态原生集成）** |
| Spring AI | - | AI诊断 / 情绪分析 / 拍照识宠 |
| Knife4j | - | API文档 |
| WebSocket | - | 问诊实时聊天（模拟医生自动回复） |
| Docker Compose | - | 一键部署 |

---

## 三、微服务拆分

```
exopet-cloud （根项目）
│
├── exopet-gateway          Gateway 网关服务（端口 8080）
├── exopet-auth             认证中心（端口 9200）
├── exopet-common           公共模块（工具类 / 统一返回 / 全局异常 / Swagger）
│
├── exopet-user             用户服务（用户CRUD / 地址管理 / 登录注册）
├── exopet-consult          问诊服务（医生管理 / 问诊订单 / WebSocket实时聊天 / 评价）
├── exopet-ai               AI诊断服务（Spring AI + 大模型 / 情绪分析 / 识宠）
├── exopet-pet              宠物服务（宠物档案 / 病例管理 / 健康记录 / 提醒）
├── exopet-hospital         医院服务（医院CRUD / 预约 / 评价）
│
├── exopet-notification     消息通知服务（RocketMQ消费者 / 推送通知）
│
└── exopet-monitor          监控中心（Spring Boot Admin）
```

### 服务间调用关系

```
客户端 → Gateway → Auth(鉴权) → 业务服务
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
                 Redis 缓存    RocketMQ 消息队列   MySQL 数据库
```

---

## 四、中间件

### 4.1 RocketMQ 消息设计（替代原 Kafka）

| Topic | 生产者 | 消费者 | 用途 |
|-------|--------|--------|------|
| `notification-push` | 各业务服务 | exopet-notification | 推送通知 |
| `consult-remind` | exopet-consult | exopet-notification | 问诊提醒 |
| `pet-health-remind` | exopet-pet | exopet-notification | 宠物健康/复查提醒 |

### 4.2 Redis 缓存设计

| Key 模式 | 用途 | 过期时间 |
|----------|------|---------|
| `exopet:token:{userId}` | JWT Token | 7天 |
| `exopet:doctor:{id}` | 医生信息 | 30分钟 |
| `exopet:pet:case:{id}` | 病例缓存 | 1小时 |

---

## 五、数据库设计

**策略：一个库 + 按模块分包**

```sql
-- 库名: exopet（单库）
-- 表前缀: 按 module 逻辑隔离

module_user/        # user, user_address
module_consult/     # doctor, consult_order, consult_message, doctor_review
module_ai/          # ai_diagnosis_record
module_pet/         # pet, medical_case, health_record, reminder_plan
module_hospital/    # hospital, hospital_appointment, hospital_review
```

**共 16 张表，详见 `exopet-db.sql`**

---

## 六、核心业务亮点

### 6.1 AI智能问诊（Spring AI + 大模型）

```
用户拍照 → 上传图片 → Spring AI ChatClient → 大模型(GPT-4o/Qwen-VL) → 诊断结果
```

- 支持上传最多6张图片
- 结构化 Prompt 输出 JSON（疾病列表 + 概率 + 养护方案）
- 置信度 < 60% 自动推荐转真人医生
- 严格免责声明（定位"参考"而非"诊断"）

### 6.2 急诊视频问诊 / WebSocket实时通讯

**视频问诊（声网 Agora）：**

```
媒体流(声网SD-RTN)    ← 不经过服务端
信令控制(后端)         ← Token签发 + 房间管理
降级策略(视频→图文)    ← 网络异常自动降级
```

**实时通讯（WebSocket）：**
- 前端通过 WebSocket 连接问诊服务
- 消息即时推送，不入轮询
- 后端自动模拟医生回复（2秒延迟）
- 聊天记录全量入库保存

### 6.3 情绪分析 / 拍照识宠

复用 AI 问诊的完整链路，仅替换 Prompt 模板，同一 `exopet-ai` 服务提供三个接口：
- `/api/ai/diagnose` — AI问诊
- `/api/ai/mood-analysis` — 情绪分析
- `/api/ai/breed-recognize` — 拍照识宠


## 七、部署架构（Docker Compose）

```
exopet-docker/
├── docker-compose.yml        # 所有服务一键启动
├── mysql/                    # MySQL 8.0
├── redis/                    # Redis 7
├── nacos/                    # Nacos Server
├── rocketmq/                 # RocketMQ NameServer + Broker + Dashboard
├── services/                 # 各微服务 Dockerfile
└── .env                      # 环境变量
```

---

## 八、项目文件结构

```
D:\xiangmu\yichong-xiaoyu\
├── exopet-cloud-architecture.md   ← 本文档
├── exopet-cloud-issues.md         ← 面试考点 + 问题记录
│
├── exopet-mobile/                 ← 前端（Vue 3 + Vant 4 + Axios + WebSocket）
│
└── exopet-cloud/                  ← 后端（已完成 6/7 业务模块）
    ├── pom.xml                    ← 聚合 POM（10个子模块）
    ├── exopet-gateway/            ← 网关（JWT鉴权 + Redis降级 + CORS）
    ├── exopet-auth/               ← 认证（JWT签发 + 登录）
    ├── exopet-common/             ← 公共模块（统一返回 / 全局异常 / ResultCode）
    ├── exopet-user/               ← ✅ 用户服务（接口+impl + XML mapper）
    ├── exopet-consult/            ← ✅ 问诊服务（4张表 + WebSocket实时通讯）
    ├── exopet-ai/                 ← ✅ AI诊断服务（Controller + Service + 对话流 + 落库 + Sentinel限流）
    ├── exopet-pet/                ← ✅ 宠物服务（4张表 + XML mapper + CRUD全套）
    ├── exopet-hospital/           ← ✅ 医院服务（3张表 + XML mapper + CRUD全套）
    ├── exopet-notification/       ← ✅ 通知服务（RocketMQ消费者 + WebSocket推送 + 通知API）
    └── exopet-monitor/            ← ❌ 空壳（仅application.yml）
```

---

## 九、前端-后端API映射

### 8.1 网关路由规则

所有前端请求统一走 `http://localhost:8080`（Gateway），由网关按路径前缀转发到对应微服务。

| 前端页面 | 前端路由 | 后端服务 | Gateway路由 |
|---------|---------|---------|------------|
| 登录页 | `/login1`, `/login2` | exopet-auth | `/auth/**` |
| 首页 | `/home` | exopet-hospital | `/hospital/**` |
| 个人中心 | `/profile` | exopet-user | `/user/**` |
| 我的爱宠 | `/my-pets` | exopet-pet | `/pet/**` |
| AI问诊 | `/ai-consult` | exopet-ai | `/api/ai/**` |
| 专人问诊 | `/specialist-consult` | exopet-consult | `/consult/**` |
| 急诊问诊 | `/emergency-consult` | exopet-consult | `/consult/**` |
| 医院查找 | `/hospital-finder` | exopet-hospital | `/hospital/**` |
| 拍照识宠 | `/pet-photo` | exopet-ai | `/api/ai/**` |
| 情绪分析 | `/mood-analysis` | exopet-ai | `/api/ai/**` |
| 对话框(WebSocket) | `/dialog` | exopet-consult | `/consult/**` + `ws://localhost:9204/ws/consult` |
| 问题反馈 | `/feedback` | exopet-user | `/user/**` |
| 通知中心 | `/notification` | exopet-notification | `/notification/**` + `ws://localhost:9209/ws/notification` |

### 8.2 完整API接口清单

#### 认证服务 — `exopet-auth`

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | `/auth/login` | 手机号+验证码登录 | `{phone, code}` | `{token, userId}` |
| POST | `/auth/logout` | 退出登录 | Header: Bearer Token | `200` |
| POST | `/auth/send-code` | 发送验证码 | `phone` | `200` |

#### 用户服务 — `exopet-user`

| 方法 | 路径 | 说明 | 请求/参数 | 响应 |
|------|------|------|-----------|------|
| GET | `/user/{id}` | 获取用户信息 | `id` | `User` |
| PUT | `/user/{id}` | 更新用户信息 | `User` JSON | `200` |
| GET | `/user/{id}/address` | 收货地址列表 | `id` | `Address[]` |
| POST | `/user/address` | 新增地址 | `Address` JSON | `200` |
| POST | `/user/feedback` | 提交反馈 | `{content, images}` | `200` |

#### 问诊服务 — `exopet-consult`（✅ 已完成 + RocketMQ通知推送）

| 方法 | 路径 | 说明 | 请求/参数 | 响应 |
|------|------|------|-----------|------|
| GET | `/consult/doctor/list` | 医生列表（启用+评分排序） | - | `Doctor[]` |
| GET | `/consult/doctor/{id}` | 医生详情 | `id` | `Doctor` |
| POST | `/consult/order` | **发起问诊**（自动生成订单号+发通知） | `{doctorId, type, petId}` | `ConsultOrder` |
| GET | `/consult/order/list/{userId}` | 用户问诊记录 | `userId` | `ConsultOrder[]` |
| GET | `/consult/order/{orderNo}` | 按单号查询问诊单 | `orderNo` | `ConsultOrder` |
| PUT | `/consult/order/{id}` | 更新问诊单 | `ConsultOrder` JSON | `200` |
| PUT | `/consult/order/{id}/status` | 更新状态（自动发通知） | `{status}` | `200` |
| POST | `/consult/message` | 发送消息 | `{consultId, senderType, content}` | `ConsultMessage` |
| GET | `/consult/message/list/{consultId}` | 消息列表 | `consultId` | `ConsultMessage[]` |
| POST | `/consult/review` | 评价医生（1-5星校验） | `{consultId, doctorId, rating}` | `DoctorReview` |
| WS | `/ws/consult` | **实时聊天（即时推送）** | - | 实时消息流 |

#### AI诊断服务 — `exopet-ai`

| 方法 | 路径 | 说明 | 请求参数 | 响应 |
|------|------|------|---------|------|
| POST | `/api/ai/diagnose` | **AI问诊** | `image(文件)+breedType+symptoms+description` | `{diseaseList, carePlan}` |
| POST | `/api/ai/mood-analysis` | **情绪分析** | `image(文件)` | `{mood, confidence, advice}` |
| POST | `/api/ai/breed-recognize` | **拍照识宠** | `image(文件)` | `{breedName, sciName, description}` |

#### 宠物服务 — `exopet-pet`（✅ 已完成）

| 方法 | 路径 | 说明 | 请求/参数 | 响应 |
|------|------|------|-----------|------|
| GET | `/pet/list` | 宠物列表 | Header: userId | `Pet[]` |
| POST | `/pet` | 添加宠物 | `Pet` JSON | `Pet` |
| GET | `/pet/{id}` | 宠物详情 | `id` | `Pet` |
| PUT | `/pet/{id}` | 更新宠物 | `Pet` JSON | `200` |
| DELETE | `/pet/{id}` | 删除宠物 | `id` | `200` |
| POST | `/pet/health-record` | 添加健康记录 | `HealthRecord` JSON | `HealthRecord` |
| GET | `/pet/health-record/{id}` | 健康记录详情 | `id` | `HealthRecord` |
| GET | `/pet/health-record/list/by-pet/{petId}` | 健康记录列表（可筛选类型） | `petId` + `?recordType=` | `IPage<HealthRecord>` |
| PUT | `/pet/health-record/{id}` | 更新健康记录 | `HealthRecord` JSON | `200` |
| DELETE | `/pet/health-record/{id}` | 删除健康记录 | `id` | `200` |
| POST | `/pet/case` | 新增病例 | `MedicalCase` JSON | `MedicalCase` |
| GET | `/pet/case/{id}` | 病例详情 | `id` | `MedicalCase` |
| GET | `/pet/case/list/by-pet/{petId}` | 按宠物查病例（分页） | `petId` + `?page=&size=` | `IPage<MedicalCase>` |
| GET | `/pet/case/list/by-user/{userId}` | 按用户查病例 | `userId` | `IPage<MedicalCase>` |
| GET | `/pet/case/list` | 多条件组合查询 | `?severity=&keyword=&startDate=` | `IPage<MedicalCase>` |
| PUT | `/pet/case/{id}` | 更新病例 | `MedicalCase` JSON | `200` |
| DELETE | `/pet/case/{id}` | 删除病例 | `id` | `200` |

#### 医院服务 — `exopet-hospital`

| 方法 | 路径 | 说明 | 请求/参数 | 响应 |
|------|------|------|-----------|------|
| GET | `/hospital/list` | 附近医院 | `?lat=&lng=&distance=&category=&page=` | `Page<Hospital>` |
| GET | `/hospital/{id}` | 医院详情 | `id` | `Hospital` |
| POST | `/hospital/appointment` | 预约就诊 | `{hospitalId, date, timeSlot, petId}` | `200` |

#### 通知服务 — `exopet-notification`（✅ 已完成）

| 方法 | 路径 | 说明 | 请求/参数 | 响应 |
|------|------|------|-----------|------|
| GET | `/notification/list/{userId}` | 通知列表（分页+已读筛选） | `userId + ?page&size&isRead` | `IPage<Notification>` |
| GET | `/notification/unread/{userId}` | 未读通知数 | `userId` | `{count}` |
| PUT | `/notification/read/{id}` | 标记单条已读 | `id + ?userId=` | `200` |
| PUT | `/notification/read-all/{userId}` | 全部标记已读 | `userId` | `200` |
| POST | `/notification/test/{userId}` | 生成测试通知 | `userId` | `Notification` |
| WS | `/ws/notification` | **实时通知推送** | `ws://...?userId=1` | 实时推送 |

### 8.3 统一返回格式

```json
// 成功
{ "code": 200, "msg": "操作成功", "data": { ... } }

// 失败
{ "code": 500, "msg": "错误信息", "data": null }

// 未登录
{ "code": 401, "msg": "未登录或Token已过期", "data": null }
```

### 8.4 前端对接方案

前端通过 Vite 代理转发到各后端服务，本地开发无需启动 Gateway：

```javascript
// src/utils/request.js
import axios from 'axios'

const request = axios.create({
  baseURL: '/api-proxy',  // Vite 代理前缀
  timeout: 10000
})

// 请求拦截器 — 自动带 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器 — 包装统一返回格式 { code, msg, data }
request.interceptors.response.use(
  res => { /* 自动解包 data */ },
  err => { /* 401 跳登录 */ }
)

export default request
```

Vite 代理配置（`vite.config.js`）：

| 路径前缀 | 目标服务 | 端口 |
|---------|---------|------|
| `/consult/**` | exopet-consult | 9204 |
| `/pet/**` | exopet-pet | 9206 |
| `/auth/**` | exopet-auth | 9200 |
| `/user/**` | exopet-user | 9201 |
| `/api/ai/**` | exopet-ai | 9205 |
| `/hospital/**` | exopet-hospital | 9207 |

### 8.5 前端API对接状态

| 前端页面 | 当前数据源 | 需对接的API | 状态 |
|---------|-----------|------------|------|
| Login1 | 直接跳转 | `POST /auth/login` | ❌ Mock（后端✅就绪） |
| Login2 | 直接跳转 | `POST /auth/login` | ❌ Mock（后端✅就绪） |
| **MyPets** | **后端 API** | `GET /pet/list` + `POST /pet/health-record` | ✅ **已对接** |
| **SpecialistConsult** | **后端 API** | `GET /consult/doctor/list` + `POST /consult/order` | ✅ **已对接** |
| **Dialog** | **后端 API** | `GET /consult/order` + WebSocket | ✅ **已对接** |
| HospitalFinder | `hospitals[]` 硬编码 | `GET /hospital/list` | ❌ Mock（后端✅就绪） |
| AIConsult | 后端 API | `POST /api/ai/diagnose`（对话式，支持图文） | ✅ **已对接** |
| MoodAnalysis | 后端 API | `POST /api/ai/mood-analysis`（对话式） | ✅ **已对接** |
| PetPhotoRecognition | 后端 API | `POST /api/ai/breed-recognize`（对话式） | ✅ **已对接** |
| 其余页面 | 静态UI | 对应API | ❌ Mock |

---

## 十、开发路线（实际进度）

| 阶段 | 任务 | 产出 | 状态 |
|------|------|------|------|
| Day 1 | 项目骨架 + Nacos + Gateway | 可启动的空项目 | ✅ **已完成** |
| Day 2 | Auth认证 + Common公共模块 | JWT签发 + 统一返回/异常 | ✅ **已完成** |
| Day 3 | 用户 + 宠物 服务 | 接口+impl规范 + XML mapper + 分页 | ✅ **已完成** |
| Day 4 | 问诊服务 + 病例管理 + WebSocket | 医生/问诊/消息/评价 + 实时通讯 | ✅ **已完成** |
| Day 5 | Spring AI 诊断服务 | AI问诊 + 情绪分析 + 识宠 + 对话流 + 落库 | ✅ **已完成** |
| Day 6 | 前端对接（去Mock） | MyPets/Specialist/Dialog 已对接 | ✅ **3个页面已完成** |
| Day 7 | 医院服务 + 评价系统 | 医院CRUD + 预约 + 评价（3张表） | ✅ **已完成** |
| Day 8 | Gateway 统一入口 + Nacos注册 | 全服务注册到Nacos，前端走Gateway | ✅ **已完成** |
| Day 9 | RocketMQ 替换 Kafka + 完善配置 | docker-compose + notification 配置 | ✅ **已完成** |
| Day 10 | 通知服务 + exopet-monitor | RocketMQ消费者 + WebSocket推送 + 通知API | ✅ **已完成（通知服务）** |
| Day 11 | 前端对接（医院+AI+通知） | HospitalFinder/MoodAnalysis/AIConsult + 通知中心 | ⏳ **待完成** |

---

## 十一、面试话术汇总

| 面试官问 | 回答 |
|---------|------|
| 为什么用微服务？ | "按业务领域拆分为9个独立服务，比如医院服务挂了不影响AI问诊，各服务独立部署独立扩缩容。" |
| 跨服务调用怎么做？ | "同步调用用 OpenFeign + Nacos 负载均衡，异步用 RocketMQ 解耦。比如问诊完成后通过 RocketMQ 异步推送通知。" |
| 分布式事务？ | "核心链路上用 Seata AT模式保证问诊和病例的最终一致性。非核心场景用 RocketMQ 事务消息+本地事务表+重试机制。" |
| 缓存策略？ | "Redis 缓存医生信息、病例数据和 Token。缓存失效采用随机 TTL + 互斥锁双重防止缓存雪崩和击穿。" |
| AI怎么做？ | "Spring AI 统一封装。目前用硅基流动的视觉模型 Qwen3-VL-8B-Instruct，支持图文输入。对话式诊断流程：用户描述→AI追问→再追问→最终结论。Prompt 结构化输出 JSON。" |
| 视频怎么实现？ | "音视频媒体流走声网全球网络，不经过我服务器。我后端只做 Token 签发和房间管理。网络差时自动降级图文问诊。" |
| **网关鉴权怎么做的？** | "Gateway 拦截请求，**优先从 Redis 校验 Token**，Redis 挂了自动**降级为 JWT 本地验签**，保证服务不中断。" |
| **实时通讯怎么实现的？** | "问诊聊天走 **WebSocket**，消息即时推送，后端存库保存聊天记录。还模拟了2秒后医生自动回复，演示通讯链路。" |

---

## 十二、本地启动与关闭

### 启动顺序

```bash
# 1. 启动基础设施（Docker）
cd D:\xiangmu\yichong-xiaoyu\exopet-cloud
docker compose up -d          # 全部启动
docker compose up -d redis    # 只启动 Redis

# 2. 启动后端微服务（在 exopet-cloud 目录下）
mvn spring-boot:run -pl exopet-auth

# 3. 启动前端（在 exopet-mobile 目录下）
cd D:\xiangmu\yichong-xiaoyu\exopet-mobile
npm run dev
```

### 关闭

```bash
# 停 Docker 容器（保留数据）
docker compose stop           # 停止所有
docker compose stop redis     # 只停 Redis

# 停 Docker 容器并删掉（清数据）
docker compose down           # 全部删除
docker compose down redis     # 只删 Redis

# 停后端微服务
# IDEA 里点红色方块，或终端按 Ctrl+C

# 停前端
# 终端按 Ctrl+C
```

### 各服务地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:3000 |
| exopet-auth | http://localhost:9200 |
| Redis | localhost:6379 |
| Nacos | http://localhost:8848 |
| RocketMQ | localhost:9876（NameServer） |

---

> **作者：** 程浩男
> **最后更新：** 2026-07-30（Day 10 已完成）
> **状态：** 后端 6/7 业务模块已完成 + 前端 3 个页面已对接 + WebSocket 实时通讯 + RocketMQ 通知推送