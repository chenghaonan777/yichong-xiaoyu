# ExoPet 异宠小愈 — 前端API对接文档（医院 + AI + 通知篇）

> 本文档面向前端同学，包含 **医院模块**、**AI模块** 和 **通知模块** 的完整 API 说明、请求/响应示例、前端对接注意事项。
>
> 基础路径：所有请求走 Vite 代理，无需启动 Gateway
>
> | 服务 | 路径前缀 | 目标端口 |
> |------|---------|---------|
> | exopet-hospital | `/hospital/**` | `9207` |
> | exopet-ai | `/api/ai/**` | `9205` |
> | exopet-notification | `/notification/**` + `ws://` | `9209` |
>
> 统一返回格式：`{ code: number, msg: string, data: T }`

---

## 目录

- [一、统一说明](#一统一说明)
- [二、医院模块 API](#二医院模块-api)
  - [1. 医院列表（分页+搜索）](#1-医院列表分页搜索)
  - [2. 医院详情](#2-医院详情)
  - [3. 预约就诊](#3-预约就诊)
  - [4. 用户预约列表](#4-用户预约列表)
  - [5. 提交医院评价](#5-提交医院评价)
  - [6. 医院评价列表](#6-医院评价列表)
- [三、AI模块 API](#三ai模块-api)
  - [1. AI问诊（对话式）](#1-ai问诊对话式)
  - [2. 情绪分析（对话式）](#2-情绪分析对话式)
  - [3. 拍照识宠（对话式）](#3-拍照识宠对话式)
  - [4. 三接口通用设计说明](#4-三接口通用设计说明)
- [四、通知模块 API](#四通知模块-api)
  - [1. 通知列表](#1-通知列表)
  - [2. 未读通知数](#2-未读通知数)
  - [3. 标记单条已读](#3-标记单条已读)
  - [4. 全部标记已读](#4-全部标记已读)
  - [5. WebSocket 实时推送](#5-websocket-实时推送)
- [五、典型对接场景](#五典型对接场景)
  - [场景一：医院查找页](#场景一医院查找页)
  - [场景二：AI问诊对话流](#场景二ai问诊对话流)
  - [场景三：情绪分析](#场景三情绪分析)
  - [场景四：通知中心](#场景四通知中心)

---

## 一、统一说明

### 请求头

| Header | 说明 | 必填 |
|--------|------|------|
| `userId` | 用户ID（当前未接 Auth 时用 header 传递） | **是**（AI模块） |
| `Authorization` | `Bearer {token}`（后续接入 Auth 后） | 后续 |

### 统一返回格式

```typescript
// 成功
{ code: 200, msg: "操作成功", data: { ... } }

// 失败
{ code: 500, msg: "错误信息", data: null }

// 限流/降级（AI模块专有）
{ code: 500, msg: "AI诊断当前繁忙，请稍后重试", data: null }

// 未登录
{ code: 401, msg: "未登录或Token已过期", data: null }
```

### 分页参数通用约定

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | int | 1 | 页码 |
| `size` | int | 10~20 | 每页条数 |

### 分页响应结构

```typescript
{
  code: 200,
  msg: "操作成功",
  data: {
    records: [...],      // 当前页数据
    total: number,       // 总记录数
    size: number,        // 每页条数
    current: number,     // 当前页码
    pages: number        // 总页数
  }
}
```

---

## 二、医院模块 API

> 端口：9207 / 路径前缀：`/hospital`

### 1. 医院列表（分页+搜索）

**按品类筛选 + 关键词搜索 + 分页**

```
GET /hospital/list?category=爬行类&keyword=北京&page=1&size=20
```

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `category` | string | 否 | - | 接诊品类，如 `爬行类` `鸟类` `小型哺乳` |
| `keyword` | string | 否 | - | 关键词（匹配医院名称/地址） |
| `page` | int | 否 | 1 | 页码 |
| `size` | int | 否 | 20 | 每页条数 |

**响应 data 说明**：分页对象，records 中每项为 `Hospital` 对象

**示例请求**：
```
GET /hospital/list?category=爬行类&page=1&size=10
```

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "爱诺异宠医院",
        "address": "北京市朝阳区望京街道XX号",
        "latitude": 39.9865,
        "longitude": 116.4811,
        "phone": "010-88886666",
        "businessHours": "09:00-21:00",
        "coverImage": "/images/hospital/cover1.jpg",
        "images": "[\"/images/hospital/img1.jpg\",\"/images/hospital/img2.jpg\"]",
        "rating": 4.8,
        "reviewCount": 126,
        "expertiseTags": "[\"爬行类\",\"鸟类\"]",
        "licenseImage": "/images/hospital/license1.jpg",
        "intro": "专注异宠诊疗15年，拥有先进的爬宠专科设备",
        "status": 1,
        "createdAt": "2026-07-27T17:38:00",
        "updatedAt": "2026-07-27T17:38:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 医院详情

```
GET /hospital/{id}
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 医院ID |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "爱诺异宠医院",
    "address": "北京市朝阳区望京街道XX号",
    "latitude": 39.9865,
    "longitude": 116.4811,
    "phone": "010-88886666",
    "businessHours": "09:00-21:00",
    "coverImage": "/images/hospital/cover1.jpg",
    "images": "[\"/images/hospital/img1.jpg\",\"/images/hospital/img2.jpg\"]",
    "rating": 4.8,
    "reviewCount": 126,
    "expertiseTags": "[\"爬行类\",\"鸟类\"]",
    "licenseImage": "/images/hospital/license1.jpg",
    "intro": "专注异宠诊疗15年，拥有先进的爬宠专科设备",
    "status": 1,
    "createdAt": "2026-07-27T17:38:00",
    "updatedAt": "2026-07-27T17:38:00"
  }
}
```

### 3. 预约就诊

```
POST /hospital/appointment
Content-Type: application/json
```

**请求体**：

```json
{
  "hospitalId": 1,
  "userId": 1,
  "petId": 1,
  "appointDate": "2026-07-30",
  "timeSlot": "10:00-11:00",
  "contactName": "张先生",
  "contactPhone": "13800138000",
  "remark": "守宫拒食一周"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalId` | Long | ✅ | 医院ID |
| `userId` | Long | ✅ | 用户ID |
| `petId` | Long | ❌ | 宠物ID |
| `appointDate` | date | ✅ | 预约日期，格式 `YYYY-MM-DD` |
| `timeSlot` | string | ✅ | 时间段，如 `10:00-11:00` |
| `contactName` | string | ❌ | 联系人姓名 |
| `contactPhone` | string | ❌ | 联系电话 |
| `remark` | string | ❌ | 备注说明 |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "hospitalId": 1,
    "userId": 1,
    "petId": 1,
    "appointDate": "2026-07-30",
    "timeSlot": "10:00-11:00",
    "contactName": "张先生",
    "contactPhone": "13800138000",
    "remark": "守宫拒食一周",
    "status": 0,
    "createdAt": "2026-07-29T12:00:00"
  }
}
```

### 4. 用户预约列表

```
GET /hospital/appointment/list/{userId}?page=1&size=10
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `userId` | Long | 用户ID |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | int | 否 | 1 | 页码 |
| `size` | int | 否 | 10 | 每页条数 |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "hospitalId": 1,
        "userId": 1,
        "petId": 1,
        "appointDate": "2026-07-30",
        "timeSlot": "10:00-11:00",
        "contactName": "张先生",
        "contactPhone": "13800138000",
        "remark": "守宫拒食一周",
        "status": 0,
        "cancelReason": null,
        "createdAt": "2026-07-29T12:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

**状态枚举**：

| 值 | 说明 | 前端展示 |
|----|------|---------|
| 0 | 待确认 | ⏳ 等待医院确认 |
| 1 | 已确认 | ✅ 预约成功 |
| 2 | 已完成 | ✅ 已就诊 |
| 3 | 已取消 | ❌ 已取消 |

### 5. 提交医院评价

```
POST /hospital/review
Content-Type: application/json
```

**请求体**：

```json
{
  "hospitalId": 1,
  "userId": 1,
  "appointId": 1,
  "rating": 5,
  "content": "医生很专业，守宫康复了"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `hospitalId` | Long | ✅ | 医院ID |
| `userId` | Long | ✅ | 用户ID |
| `appointId` | Long | ❌ | 关联预约ID |
| `rating` | int | ✅ | 评分 1~5 星 |
| `content` | string | ❌ | 评价内容 |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "hospitalId": 1,
    "userId": 1,
    "appointId": 1,
    "rating": 5,
    "content": "医生很专业，守宫康复了",
    "createdAt": "2026-07-29T12:00:00"
  }
}
```

### 6. 医院评价列表

```
GET /hospital/review/list/{hospitalId}?page=1&size=20
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `hospitalId` | Long | 医院ID |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 |
|------|------|------|--------|
| `page` | int | 否 | 1 |
| `size` | int | 否 | 20 |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "hospitalId": 1,
        "userId": 1,
        "appointId": 1,
        "rating": 5,
        "content": "医生很专业，守宫康复了",
        "createdAt": "2026-07-29T12:00:00"
      }
    ],
    "total": 1,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

---

## 三、AI模块 API

> 端口：9205 / 路径前缀：`/api/ai`

### ⚠️ 三个接口共性

| 特性 | 说明 |
|------|------|
| 请求方式 | `POST`，`multipart/form-data`（Content-Type 不要用 application/json） |
| 图片 | **可选**，最多6张建议前端分次上传，单次1张 |
| 对话 | 全部是**对话式**，前端维护 `history` JSON，每次请求携带 |
| Header | **必传** `userId: {用户ID}` |
| 限流 | 接口有 Sentinel 限流保护，触发时返回 `"AI诊断当前繁忙，请稍后重试"` |

### 1. AI问诊（对话式）

```
POST /api/ai/diagnose
Content-Type: multipart/form-data
userId: 1
```

**请求参数（form-data）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `image` | File | ❌ | 宠物图片（可选，每次最多1张） |
| `breedType` | string | ✅ | 宠物大类，如 `爬行类` `鸟类` `小型哺乳` |
| `breedName` | string | ✅ | 具体品种，如 `豹纹守宫` `牡丹鹦鹉` |
| `symptoms` | string | ✅ | 症状标签，如 `拒食,精神萎靡,软便`（逗号分隔） |
| `symptomDesc` | string | ❌ | 症状详细描述 |
| `history` | string | ❌ | **对话历史 JSON 字符串**（见下方说明） |

**对话历史格式**：

```typescript
// history 参数是一个 JSON 字符串（注意：是 string 类型，不是直接传对象）
// 每轮对话格式：
[
  { "user": "用户第一轮说的话", "ai": "AI第一轮回复" },
  { "user": "用户第二轮说的话", "ai": "AI第二轮回复" }
]
// 第一轮请求时 history 为空字符串或不传
```

**响应 data 说明**：`AiDiagnosisRecord` 对象

**第一轮请求示例**：
```
POST /api/ai/diagnose
Content-Type: multipart/form-data
userId: 1

image: (文件)
breedType: 爬行类
breedName: 豹纹守宫
symptoms: 拒食,精神萎靡
symptomDesc: 已经三天不吃东西了，平时很活跃现在不爱动
history: (空字符串或不传)
```

**第一轮响应（AI追问）**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "breedType": "爬行类",
    "breedName": "豹纹守宫",
    "symptoms": "拒食,精神萎靡",
    "symptomDesc": "已经三天不吃东西了，平时很活泼现在不爱动",
    "images": "[\"shougong.jpg\"]",
    "aiModel": "Qwen3-VL-8B-Instruct",
    "aiRawResponse": "{\"reply\":\"根据你描述的情况，豹纹守宫拒食伴随精神萎靡，可能有几种原因：环境温度偏低导致代谢下降、肠道寄生虫感染，或者处于发情期。\",\"nextQuestion\":\"请问最近是否有给守宫测量环境温度？排便情况如何？\",\"isDone\":false}",
    "diseaseList": "{\"reply\":\"根据你描述的情况...\",\"nextQuestion\":\"请问最近...\",\"isDone\":false}",
    "confidence": 0.80,
    "durationMs": 2850,
    "createdAt": "2026-07-29T12:00:00"
  }
}
```

**第二轮请求（用户回复后）**：
```
POST /api/ai/diagnose
Content-Type: multipart/form-data
userId: 1

breedType: 爬行类
breedName: 豹纹守宫
symptoms: 拒食,精神萎靡
symptomDesc: 温度28度，没有排便
history: [{"user":"已经三天不吃东西了，平时很活泼现在不爱动","ai":"根据你描述的情况...请问最近是否有给守宫测量环境温度？排便情况如何？"}]
```

**第二轮响应（AI最终结论）**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "userId": 1,
    "aiRawResponse": "{\"reply\":\"温度28℃对于豹纹守宫来说偏低（适宜温度30-32℃），低温会导致消化功能减缓进而拒食。没有排便也印证了消化系统处于停滞状态。\",\"nextQuestion\":\"\",\"isDone\":true,\"conclusion\":{\"possibleDiseases\":[{\"name\":\"低温引起的代谢减缓（非病理性）\",\"probability\":0.85,\"severity\":\"低\"},{\"name\":\"轻度肠胃功能紊乱\",\"probability\":0.15,\"severity\":\"中\"}],\"carePlan\":{\"temperature\":\"将环境温度提升至30-32℃，使用加热垫或陶瓷加热灯\",\"diet\":\"升温后24小时内不要喂食，等活跃度恢复后再尝试喂食面包虫\",\"medication\":\"暂不需要用药，如果升温3天后仍拒食建议就诊\"},\"confidence\":0.85}}",
    "diseaseList": "{\"reply\":\"温度28℃...\",\"nextQuestion\":\"\",\"isDone\":true,\"conclusion\":{...}}",
    "confidence": 0.85,
    "durationMs": 3100,
    "createdAt": "2026-07-29T12:00:10"
  }
}
```

### 前端对接指南 — AI问诊对话式

```typescript
// 推荐的前端实现方式

interface ChatTurn {
  user: string;
  ai: string;
}

// 判断当前轮次是否结束
function isDone(record: AiDiagnosisRecord): boolean {
  const raw = JSON.parse(record.aiRawResponse);
  return raw.isDone === true;
}

// 获取AI的回复内容
function getReply(record: AiDiagnosisRecord): string {
  const raw = JSON.parse(record.aiRawResponse);
  return raw.reply;
}

// 获取AI追问的问题（isDone为false时存在）
function getNextQuestion(record: AiDiagnosisRecord): string {
  const raw = JSON.parse(record.aiRawResponse);
  return raw.nextQuestion || '';
}

// 获取最终结论（isDone为true时存在）
function getConclusion(record: AiDiagnosisRecord) {
  const raw = JSON.parse(record.aiRawResponse);
  return raw.conclusion;
}

// 维护对话历史
let history: ChatTurn[] = [];

// 第一轮：不传history
async function firstRound() {
  const res = await api.diagnose({ ...formData });
  const data = res.data;
  // 保存对话到历史
  history.push({
    user: formData.symptomDesc,
    ai: getReply(data)
  });
  // 如果 !isDone(data)，显示AI的追问
  return data;
}

// 后续轮次：传history
async function nextRound(userReply: string) {
  const res = await api.diagnose({
    ...formData,
    symptomDesc: userReply,  // 用户本轮回复
    history: JSON.stringify(history)
  });
  const data = res.data;
  history.push({
    user: userReply,
    ai: getReply(data)
  });
  return data;
}
```

### 2. 情绪分析（对话式）

```
POST /api/ai/mood-analysis
Content-Type: multipart/form-data
userId: 1
```

**请求参数（form-data）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `image` | File | ❌ | 宠物图片（可选） |
| `description` | string | ✅ | 宠物行为描述 |
| `history` | string | ❌ | 对话历史 JSON 字符串（格式同问诊） |

**示例请求（第一轮）**：
```
POST /api/ai/mood-analysis
Content-Type: multipart/form-data
userId: 1

image: (文件)
description: 我家鹦鹉最近总是拔自己的羽毛，脾气也变得暴躁
```

**示例响应（AI追问）**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 3,
    "userId": 1,
    "aiRawResponse": "{\"reply\":\"牡丹鹦鹉拔羽并伴随暴躁情绪，常见原因包括：环境压力、缺乏陪伴、营养不均衡或皮肤病。\",\"nextQuestion\":\"请问这种情况持续多久了？它平时每天出笼活动的时间有多久？\",\"isDone\":false}",
    "confidence": 0.80,
    "durationMs": 2650,
    "createdAt": "2026-07-29T12:00:00"
  }
}
```

**最终结论响应（isDone: true 时的 conclusion 结构）**：
```json
{
  "mood": "焦虑/压力过大",
  "confidence": 0.88,
  "advice": "建议增加每日出笼活动时间至2小时以上，提供益智玩具分散注意力，检查饮食是否缺乏维生素，必要时就诊排查皮肤病"
}
```

### 3. 拍照识宠（对话式）

```
POST /api/ai/breed-recognize
Content-Type: multipart/form-data
userId: 1
```

**请求参数（form-data）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `image` | File | ❌ | 宠物图片（可选） |
| `description` | string | ✅ | 宠物外形特征描述 |
| `history` | string | ❌ | 对话历史 JSON 字符串 |

**示例请求（第一轮）**：
```
POST /api/ai/breed-recognize
Content-Type: multipart/form-data
userId: 1

image: (文件)
description: 朋友送的一只小蜥蜴，全身绿色，尾巴可以卷起来
```

**最终结论响应（isDone: true 时的 conclusion 结构）**：
```json
{
  "breedName": "绿鬣蜥",
  "sciName": "Iguana iguana",
  "description": "绿鬣蜥是一种常见的树栖爬行动物，原产于中南美洲，以植物为食",
  "characteristics": ["全身绿色","尾部可卷曲","喉部有垂肉","成年体型较大"]
}
```

### 4. 三接口通用设计说明

#### 响应字段 `aiRawResponse` 的 JSON 结构

```typescript
// 中间轮（isDone: false）
interface AiIntermediateResponse {
  reply: string;          // AI回复内容 → 展示给用户
  nextQuestion: string;   // AI追问 → 展示给用户，作为输入提示
  isDone: false;
}

// 最终轮（isDone: true）
interface AiFinalResponse {
  reply: string;          // AI总结
  nextQuestion: "";
  isDone: true;
  conclusion: AiDiagnosisConclusion;
}

// 问诊结论
interface AiDiagnosisConclusion {
  possibleDiseases: Array<{
    name: string;         // 疾病名称
    probability: number;  // 概率 0~1
    severity: string;     // 严重程度: 低/中/高
  }>;
  carePlan: {
    temperature?: string; // 温度建议
    diet?: string;        // 饮食建议
    medication?: string;  // 用药建议
  };
  confidence: number;     // 综合置信度 0~1
}

// 情绪分析结论
interface AiMoodConclusion {
  mood: string;           // 情绪判断
  confidence: number;     // 置信度
  advice: string;         // 养护建议
}

// 识宠结论
interface AiBreedConclusion {
  breedName: string;      // 品种名称
  sciName: string;        // 学名
  description: string;    // 品种说明
  characteristics: string[]; // 特征列表
}
```

#### 前端取数据路径

```typescript
// 所有响应存在 data.aiRawResponse 字段中（JSON 字符串）
const raw = JSON.parse(response.data.aiRawResponse);

// 1. 获取AI回复文案
const reply = raw.reply;           // 三个接口都有

// 2. 是否结束
const done = raw.isDone;           // false → AI在追问，true → 有最终结论

// 3. 获取追问
const nextQ = raw.nextQuestion;    // 用于展示在输入框的 placeholder

// 4. 获取结论（isDone=true时）
if (done) {
  const conclusion = raw.conclusion;

  // 问诊场景
  conclusion.possibleDiseases;     // 疾病列表
  conclusion.carePlan;             // 养护方案

  // 情绪分析场景
  conclusion.mood;                 // 情绪
  conclusion.advice;               // 建议

  // 识宠场景
  conclusion.breedName;            // 品种
  conclusion.sciName;              // 学名
  conclusion.characteristics;      // 特征

  // 通用的置信度
  const confidence = conclusion.confidence; // 0~1
}
```

---

## 四、通知模块 API

> 端口：9209 / 路径前缀：`/notification` / WebSocket：`ws://localhost:9209/ws/notification`

### 1. 通知列表

```
GET /notification/list/{userId}?page=1&size=20&isRead=
```

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| `userId` | Long | 用户ID |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | int | 否 | 1 | 页码 |
| `size` | int | 否 | 20 | 每页条数 |
| `isRead` | bool | 否 | - | 筛选已读/未读，不传=全部 |

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "type": "SYSTEM",
        "title": "欢迎注册",
        "content": "欢迎来到异宠小愈！",
        "relatedId": null,
        "isRead": 0,
        "readAt": null,
        "createdAt": "2026-07-30T12:00:00"
      }
    ],
    "total": 1,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

### 2. 未读通知数

```
GET /notification/unread/{userId}
```

**示例响应**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { "count": 3 }
}
```

### 3. 标记单条已读

```
PUT /notification/read/{id}?userId=1
```

**示例响应**：
```json
{ "code": 200, "msg": "操作成功", "data": null }
```

### 4. 全部标记已读

```
PUT /notification/read-all/{userId}
```

**示例响应**：
```json
{ "code": 200, "msg": "操作成功", "data": null }
```

### 5. WebSocket 实时推送

前端连接：
```javascript
const ws = new WebSocket('ws://localhost:9209/ws/notification?userId=1');

ws.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  // notification 结构同通知列表的 records 项
  console.log('收到通知：', notification.title);
  // 刷新未读计数或弹窗提示
};
```

**触发时机**：后端收到 RocketMQ 消息后，自动推送给对应用户。

**通知类型**：

| type 值 | 说明 | 来源 |
|---------|------|------|
| `SYSTEM` | 系统通知 | 注册欢迎等 |
| `CONSULT` | 问诊提醒 | 问诊单状态变更 |
| `HEALTH` | 健康提醒 | 复查/用药到期提醒 |

**前端典型流程**：
1. 页面加载 → `GET /notification/unread/{userId}` 获取未读数，显示红点
2. 打开通知中心 → `GET /notification/list/{userId}` 加载列表
3. WebSocket 收到推送 → 未读数 +1，列表顶部插入新通知
4. 点击通知 → `PUT /notification/read/{id}` 标记已读
5. 点"全部已读" → `PUT /notification/read-all/{userId}`

---

## 五、典型对接场景

### 场景一：医院查找页

```
HospitalFinder.vue

1. 页面加载 → GET /hospital/list?page=1&size=20
   - 展示医院卡片列表（封面图、名称、评分、接诊品类标签）
   - 注意：expertiseTags 是 JSON 字符串数组，需 JSON.parse 后使用

2. 顶部品类筛选 → GET /hospital/list?category=鸟类&page=1&size=20
   - 切换品类重新请求

3. 搜索 → GET /hospital/list?keyword=朝阳&page=1&size=20
   - 防抖搜索

4. 点击医院 → GET /hospital/{id}
   - 跳转医院详情页
   - 展示：基础信息、环境图(images JSON.parse)、评价列表

5. 预约按钮 → POST /hospital/appointment
   - 弹出预约表单

6. 评价列表 → GET /hospital/review/list/{hospitalId}?page=1&size=20
```

### 场景二：AI问诊对话流

```
AIConsult.vue 或 dialog 页

数据结构：
- messages: Array<{ role: 'user'|'ai', content: string, isFinal?: boolean }>
- history: Array<{ user: string, ai: string }>  // 传给后端的对话历史
- currentResponse: { reply, nextQuestion, isDone, conclusion }

流程：
1. 用户填写症状描述（breedType / breedName / symptoms / symptomDesc / 可选图片）

2. 首次请求 → POST /api/ai/diagnose（无 history）
   - 收到响应 → messages.push({ role: 'ai', content: raw.reply })
   - 如果 !raw.isDone → 显示 raw.nextQuestion 作为输入框提示
   - history.push({ user: symptomDesc, ai: raw.reply })

3. 用户回复 → 组装新一轮请求
   - symptomDesc = 用户本轮输入（覆盖）
   - history = JSON.stringify(history)
   - 发送 → POST /api/ai/diagnose

4. 重复直到 raw.isDone = true
   - 展示最终结论：疾病列表 + 养护方案 + 置信度
   - 置信度 < 60% 时提示"建议转诊真人医生"
```

### 场景三：情绪分析

```
MoodAnalysis.vue

与问诊流程完全相同，区别：
- 接口：POST /api/ai/mood-analysis
- 请求字段：description + 可选 image + history
- 最终结论结构不同：{ mood, confidence, advice }
- 前端展示：情绪标签 + 置信度 + 养护建议
```

### 场景四：通知中心

```
NotificationCenter.vue 或 Profile 页

数据流：
  页面加载
    → GET /notification/unread/{userId}  → 显示红点徽标
    → 建立 WebSocket: ws://.../ws/notification?userId=1
  
  打开通知列表
    → GET /notification/list/{userId}?page=1&size=20
    → 按时间倒序展示通知列表
  
  WebSocket 收到推送
    → 未读数 +1
    → 列表顶部插入新通知（带入场动画）
    → 可选的弹窗/Toast 提示
  
  点击单条通知
    → PUT /notification/read/{id}?userId=1
    → 标记为已读（样式变化）
    → 根据 type + relatedId 跳转: 
      CONSULT → 跳转问诊详情
      HEALTH  → 跳转宠物健康页
  
  点击"全部已读"
    → PUT /notification/read-all/{userId}
    → 列表全部变已读样式
    → 红点消失

通知类型展示：
  SYSTEM  → 灰色图标
  CONSULT → 蓝色图标  
  HEALTH  → 绿色图标
```

