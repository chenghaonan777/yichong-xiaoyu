/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : exopet

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 29/07/2026 23:15:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_diagnosis_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_diagnosis_record`;
CREATE TABLE `ai_diagnosis_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '诊断记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `consult_id` bigint NULL DEFAULT NULL COMMENT '关联问诊订单ID',
  `breed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物大类',
  `breed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品种名称',
  `symptoms` json NULL COMMENT '用户勾选症状标签数组',
  `symptom_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户补充文字描述',
  `images` json NULL COMMENT '上传图片URL数组',
  `ai_model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用的AI模型(qwen/gpt/ollama)',
  `ai_raw_response` json NULL COMMENT 'AI原始响应(全量)',
  `disease_list` json NULL COMMENT '诊断结果: 疾病列表[{name,probability,severity,detail}]',
  `care_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '养护方案(纯文本)',
  `confidence` decimal(3, 2) NULL DEFAULT NULL COMMENT '整体置信度',
  `user_feedback` tinyint(1) NULL DEFAULT NULL COMMENT '用户反馈: 1有用 0无用',
  `duration_ms` int NULL DEFAULT NULL COMMENT 'AI诊断耗时(毫秒)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_consult_id`(`consult_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI诊断记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_diagnosis_record
-- ----------------------------
INSERT INTO `ai_diagnosis_record` VALUES (16, 2, NULL, NULL, NULL, NULL, NULL, NULL, 'Qwen3-VL-8B-Instruct', '{\"reply\": \"感谢你的描述。乌龟拉不出屎（便秘）是一个常见但需重视的问题，可能由饮食结构、环境温度或脱水引起。为了进一步判断，请告诉我：1. 乌龟最近的排泄情况（频率、粪便形态）？2. 它的精神状态（是否活跃、食欲如何）？\", \"isDone\": false, \"nextQuestion\": \"乌龟最近的排泄情况（频率、粪便形态）？\"}', '{\"reply\": \"感谢你的描述。乌龟拉不出屎（便秘）是一个常见但需重视的问题，可能由饮食结构、环境温度或脱水引起。为了进一步判断，请告诉我：1. 乌龟最近的排泄情况（频率、粪便形态）？2. 它的精神状态（是否活跃、食欲如何）？\", \"isDone\": false, \"nextQuestion\": \"乌龟最近的排泄情况（频率、粪便形态）？\"}', NULL, 0.80, NULL, 2247, '2026-07-29 17:10:49');

-- ----------------------------
-- Table structure for consult_message
-- ----------------------------
DROP TABLE IF EXISTS `consult_message`;
CREATE TABLE `consult_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `consult_id` bigint NOT NULL COMMENT '问诊订单ID',
  `sender_type` tinyint(1) NOT NULL COMMENT '发送者类型: 1用户 2医生 3系统',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送者用户/医生ID',
  `msg_type` tinyint(1) NULL DEFAULT 1 COMMENT '消息类型: 1文字 2图片 3语音 4系统提示 5处方卡片',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_consult_id`(`consult_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问诊消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_message
-- ----------------------------
INSERT INTO `consult_message` VALUES (9, 10, 1, NULL, 1, '你好', '2026-07-27 14:40:32');
INSERT INTO `consult_message` VALUES (10, 10, 2, NULL, 1, '收到您的消息，请稍等，我正在查看您宠物的症状...', '2026-07-27 14:40:34');
INSERT INTO `consult_message` VALUES (11, 11, 1, NULL, 1, '11', '2026-07-27 18:01:27');
INSERT INTO `consult_message` VALUES (12, 11, 2, NULL, 1, '收到您的消息，请稍等，我正在查看您宠物的症状...', '2026-07-27 18:01:29');
INSERT INTO `consult_message` VALUES (13, 12, 1, NULL, 1, '你好', '2026-07-29 00:02:21');
INSERT INTO `consult_message` VALUES (14, 12, 2, NULL, 1, '收到您的消息，请稍等，我正在查看您宠物的症状...', '2026-07-29 00:02:23');
INSERT INTO `consult_message` VALUES (15, 13, 1, NULL, 1, '11', '2026-07-29 08:56:10');
INSERT INTO `consult_message` VALUES (16, 13, 2, NULL, 1, '收到您的消息，请稍等，我正在查看您宠物的症状...', '2026-07-29 08:56:12');

-- ----------------------------
-- Table structure for consult_order
-- ----------------------------
DROP TABLE IF EXISTS `consult_order`;
CREATE TABLE `consult_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问诊单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问诊单号(业务唯一)',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pet_id` bigint NULL DEFAULT NULL COMMENT '宠物ID',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '医生ID(AI问诊为空)',
  `type` tinyint(1) NOT NULL COMMENT '问诊类型: 1AI问诊 2真人图文 3真人视频 4急诊',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态: 0待支付 1待接诊 2问诊中 3已完成 4已取消',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `symptom_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '症状文字描述',
  `symptom_images` json NULL COMMENT '症状图片URL数组',
  `breed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物大类(爬行类/鸟类/...)',
  `breed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '宠物品种名称',
  `diagnosis_result` json NULL COMMENT '诊断结果(JSON: 疾病列表+养护方案等)',
  `ai_consult_id` bigint NULL DEFAULT NULL COMMENT '关联AI诊断ID(AI问诊转真人时使用)',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `finished_at` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_doctor_status`(`doctor_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问诊订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consult_order
-- ----------------------------
INSERT INTO `consult_order` VALUES (9, 'W1785134209483', 1, NULL, 1, 2, 0, 29.90, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-27 14:36:50', '2026-07-27 14:36:50');
INSERT INTO `consult_order` VALUES (10, 'W1785134426233', 1, NULL, 1, 2, 0, 29.90, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-27 14:40:27', '2026-07-27 14:40:27');
INSERT INTO `consult_order` VALUES (11, 'W1785146482322', 1, NULL, 2, 2, 0, 29.90, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-27 18:01:23', '2026-07-27 18:01:23');
INSERT INTO `consult_order` VALUES (12, 'W1785254535144', 1, NULL, 1, 2, 0, 29.90, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-29 00:02:15', '2026-07-29 00:02:15');
INSERT INTO `consult_order` VALUES (13, 'W1785286565877', 1, NULL, 1, 2, 0, 29.90, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-29 08:56:06', '2026-07-29 08:56:06');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称(执业兽医师/主治兽医师)',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `cert_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执业证书编号',
  `cert_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证书图片URL',
  `years_exp` int NULL DEFAULT 0 COMMENT '从业年限',
  `expertise_tags` json NULL COMMENT '擅长品类标签(爬行类/鸟类/水族/小型哺乳)',
  `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '综合评分',
  `consult_count` int NULL DEFAULT 0 COMMENT '累计接诊数',
  `price_text` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '图文问诊价格',
  `price_video` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '视频问诊价格',
  `hospital_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属医院',
  `online_status` tinyint(1) NULL DEFAULT 0 COMMENT '在线状态 0离线 1在线 2忙碌',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_online_status`(`online_status` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1, '程浩男', '主治兽医', NULL, NULL, NULL, 10, '[\"爬行类\", \"鸟类\"]', '异宠诊疗10年经验，擅长爬行动物疾病诊治', 4.9, 1200, 29.90, 59.90, '爱诺异宠医院', 1, 1, '2026-07-25 02:43:14', '2026-07-25 02:43:14');
INSERT INTO `doctor` VALUES (2, '王贵暄', '主治兽医', NULL, NULL, NULL, 8, '[\"鸟类\", \"小型哺乳\"]', '异宠诊疗8年经验，擅长鸟类和小型哺乳动物', 4.8, 980, 29.90, 59.90, '宠颐生异宠诊疗中心', 1, 1, '2026-07-25 02:43:14', '2026-07-25 02:43:14');

-- ----------------------------
-- Table structure for doctor_review
-- ----------------------------
DROP TABLE IF EXISTS `doctor_review`;
CREATE TABLE `doctor_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `consult_id` bigint NOT NULL COMMENT '问诊订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `rating` tinyint(1) NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_consult`(`consult_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_review
-- ----------------------------

-- ----------------------------
-- Table structure for health_record
-- ----------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `pet_id` bigint NOT NULL COMMENT '宠物ID',
  `record_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录类型: vaccine/deworm/checkup/medication/weight/consult',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题(如: 狂犬疫苗第一针)',
  `record_date` date NOT NULL COMMENT '记录日期',
  `next_date` date NULL DEFAULT NULL COMMENT '下次日期(用于到期提醒)',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作兽医姓名',
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID(如问诊ID)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE,
  INDEX `idx_next_date`(`next_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康档案记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_record
-- ----------------------------
INSERT INTO `health_record` VALUES (5, 1, 'vaccine', '爬宠防疫驱虫', '2026-07-01', '2026-10-01', '爬宠专科-刘医生', '爬宠外用驱虫喷雾，环境同步消毒', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (6, 1, 'checkup', '季度体检', '2026-07-15', '2026-10-15', '爬宠专科-刘医生', '体重68g，体温适宜，活动力良好，尾部丰满度正常', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (7, 1, 'weight', '体重测量', '2026-07-10', NULL, NULL, '体重66g，进食正常', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (8, 1, 'weight', '体重测量', '2026-07-20', NULL, NULL, '体重68g，较上次增长2g，状态良好', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (9, 1, 'medication', '钙粉补充', '2026-07-05', '2026-08-05', NULL, '每周两次钙粉添加至食物中，配合维生素D3', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (10, 1, 'consult', '拒食在线问诊', '2026-07-22', NULL, '爬宠专科-刘医生', '近期温度偏低导致食欲下降，建议升温至28-30℃，观察2天', 1001, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (11, 2, 'deworm', '体内驱虫', '2026-07-05', '2026-10-05', '异宠-王医生', '龟类专用驱虫药，拌入食物投喂', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (12, 2, 'checkup', '年度体检', '2026-06-20', '2026-12-20', '异宠-王医生', '体重475g，甲壳无软甲现象，食欲旺盛，水质需保持清洁', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (13, 2, 'weight', '体重测量', '2026-07-01', NULL, NULL, '体重470g', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (14, 2, 'weight', '体重测量', '2026-07-20', NULL, NULL, '体重475g，稳定增长', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (15, 2, 'consult', '甲壳边缘发白咨询', '2026-07-18', NULL, '异宠-王医生', '正常换甲现象，无需处理，注意水质清洁', 1002, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (16, 3, 'vaccine', '禽流感疫苗', '2026-07-10', '2027-01-10', '鸟类专科-陈医生', '疫苗后观察30分钟，无异常反应', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (17, 3, 'checkup', '新宠入户体检', '2026-07-15', '2026-10-15', '鸟类专科-陈医生', '体重68g，体温40.5℃正常，羽毛光泽度良好，粪便检查无寄生虫', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (18, 3, 'weight', '体重测量', '2026-07-16', NULL, NULL, '体重68g，正常范围', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (19, 3, 'weight', '体重测量', '2026-07-25', NULL, NULL, '体重69g，采食正常', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (20, 3, 'medication', '维生素补充', '2026-07-18', '2026-08-18', NULL, '饮水添加多维电解质，连续使用5天', NULL, '2026-07-29 17:25:16');
INSERT INTO `health_record` VALUES (21, 3, 'consult', '轻微掉毛咨询', '2026-07-23', NULL, '鸟类专科-陈医生', '换羽期正常现象，增加营养供给，保持环境温度稳定', 1003, '2026-07-29 17:25:16');

-- ----------------------------
-- Table structure for hospital
-- ----------------------------
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医院ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医院名称',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度(地图标注)',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度(地图标注)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业时间(如 09:00-21:00)',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `images` json NULL COMMENT '环境图URL数组',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '综合评分',
  `review_count` int NULL DEFAULT 0 COMMENT '评价数',
  `expertise_tags` json NULL COMMENT '接诊品类标签数组',
  `license_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执业许可证照片URL',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '医院简介',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1启用 0停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_lat_lng`(`latitude` ASC, `longitude` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医院表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hospital
-- ----------------------------
INSERT INTO `hospital` VALUES (1, '爱诺异宠医院', '北京市朝阳区建国路88号', 39.9087000, 116.4716000, '010-88886666', '09:00-21:00', NULL, NULL, 4.8, 0, '[\"爬行类\", \"鸟类\", \"水族\", \"小型哺乳\"]', NULL, NULL, 1, '2026-07-25 02:43:14', '2026-07-25 02:43:14');
INSERT INTO `hospital` VALUES (2, '宠颐生异宠诊疗中心', '北京市海淀区中关村大街66号', 39.9836000, 116.3218000, '010-66668888', '09:00-20:00', NULL, NULL, 4.6, 0, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, NULL, 1, '2026-07-25 02:43:14', '2026-07-25 02:43:14');
INSERT INTO `hospital` VALUES (3, '瑞鹏宠物医院', '北京市东城区东直门外大街42号', 39.9352000, 116.4344000, '010-55557777', '08:00-22:00', NULL, NULL, 4.5, 0, '[\"鸟类\", \"水族\", \"小型哺乳\"]', NULL, NULL, 1, '2026-07-25 02:43:14', '2026-07-25 02:43:14');
INSERT INTO `hospital` VALUES (4, '北京和睦家异宠中心', '北京市朝阳区将台路2号', 39.9686000, 116.4819000, '010-84516666', '09:00-21:00', NULL, NULL, 4.9, 328, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '国内首家综合医院内设异宠专科，配备CT、超声等高端设备', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (5, '北京宠爱国际异宠医院', '北京市海淀区中关村南大街2号', 39.9527000, 116.3184000, '010-62123456', '08:30-20:00', NULL, NULL, 4.7, 215, '[\"爬行类\", \"鸟类\", \"水族\", \"节肢类\"]', NULL, '专注异宠诊疗15年，拥有独立化验室和住院部', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (6, '上海佩仕异宠诊疗中心', '上海市浦东新区世纪大道100号', 31.2348000, 121.5219000, '021-58875678', '09:00-20:00', NULL, NULL, 4.8, 456, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"水族\"]', NULL, '华东地区最大异宠专科医院，设有爬虫专属病房', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (7, '上海申生异宠医院', '上海市徐汇区衡山路10号', 31.2038000, 121.4425000, '021-64318989', '09:00-21:00', NULL, NULL, 4.6, 289, '[\"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '上海首家鸟类专科医院，拥有专业禽类诊疗团队', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (8, '上海爱侣异宠诊所', '上海市长宁区虹桥路1048号', 31.2154000, 121.3735000, '021-62686868', '09:30-19:30', NULL, NULL, 4.5, 178, '[\"爬行类\", \"水族\", \"节肢类\"]', NULL, '专注爬虫和水族类宠物诊疗', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (9, '广州维特异宠医院', '广州市天河区天河路385号', 23.1308000, 113.3223000, '020-38869888', '09:00-21:00', NULL, NULL, 4.7, 367, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '华南地区异宠诊疗标杆，设有24小时急诊', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (10, '广州瑞鹏异宠中心', '广州市海珠区江南大道中108号', 23.0932000, 113.2685000, '020-84418888', '08:30-20:00', NULL, NULL, 4.5, 234, '[\"鸟类\", \"水族\", \"小型哺乳\"]', NULL, '瑞鹏集团旗下异宠专科，医保定点单位', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (11, '深圳卡拉异宠医院', '深圳市南山区科技南路18号', 22.5362000, 113.9530000, '0755-86167890', '09:00-21:00', NULL, NULL, 4.8, 412, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"节肢类\"]', NULL, '深圳首家专业异宠医院，配备DR、彩超等先进设备', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (12, '深圳芭比堂异宠诊疗', '深圳市福田区深南大道7002号', 22.5421000, 114.0525000, '0755-82838888', '09:00-20:00', NULL, NULL, 4.6, 298, '[\"爬行类\", \"鸟类\", \"水族\"]', NULL, '专业异宠诊疗，设有独立隔离病房', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (13, '成都圣心异宠医院', '成都市武侯区人民南路三段28号', 30.6264000, 104.0563000, '028-85435888', '09:00-20:00', NULL, NULL, 4.7, 345, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '西南地区最大异宠专科医院', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (14, '成都宠爱异宠诊所', '成都市锦江区红星路三段99号', 30.6515000, 104.0825000, '028-86667890', '09:30-19:30', NULL, NULL, 4.4, 156, '[\"鸟类\", \"水族\", \"小型哺乳\"]', NULL, '社区异宠诊疗，口碑良好', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (15, '杭州虹泰异宠中心', '杭州市上城区庆春路108号', 30.2522000, 120.1752000, '0571-87228888', '09:00-21:00', NULL, NULL, 4.6, 267, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '浙江省内专业异宠诊疗机构', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (16, '杭州美联众合异宠医院', '杭州市西湖区文三路298号', 30.2749000, 120.1412000, '0571-88991199', '08:30-20:00', NULL, NULL, 4.5, 189, '[\"鸟类\", \"两栖类\", \"小型哺乳\"]', NULL, '美联集团旗下异宠专科', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (17, '南京艾贝尔异宠诊疗', '南京市玄武区中山东路198号', 32.0390000, 118.8029000, '025-84526888', '09:00-20:00', NULL, NULL, 4.6, 234, '[\"爬行类\", \"鸟类\", \"水族\"]', NULL, '南京地区异宠诊疗专业机构', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (18, '南京仁爱异宠医院', '南京市鼓楼区江东北路168号', 32.0680000, 118.7394000, '025-86299999', '09:00-19:30', NULL, NULL, 4.4, 167, '[\"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '服务贴心，价格合理', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (19, '武汉华星异宠医院', '武汉市江汉区解放大道686号', 30.5912000, 114.2786000, '027-85780001', '09:00-21:00', NULL, NULL, 4.5, 278, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '华中地区异宠医疗领先品牌', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (20, '武汉联合异宠中心', '武汉市武昌区中南路10号', 30.5359000, 114.3311000, '027-87716888', '08:30-20:00', NULL, NULL, 4.3, 145, '[\"鸟类\", \"水族\", \"节肢类\"]', NULL, '联合诊疗模式，多科室协作', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (21, '西安京和异宠医院', '西安市雁塔区长安南路300号', 34.2191000, 108.9407000, '029-85269888', '09:00-20:00', NULL, NULL, 4.5, 198, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '西北地区专业异宠诊疗', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (22, '西安瑞派异宠诊疗', '西安市未央区未央路138号', 34.3185000, 108.9396000, '029-86529999', '09:00-19:30', NULL, NULL, 4.3, 132, '[\"鸟类\", \"水族\", \"小型哺乳\"]', NULL, '设备齐全，医师经验丰富', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (23, '重庆名望异宠医院', '重庆市渝中区中山一路128号', 29.5575000, 106.5710000, '023-63838888', '09:00-20:00', NULL, NULL, 4.4, 187, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '重庆异宠诊疗行业先行者', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (24, '重庆泰和异宠诊所', '重庆市江北区建新北路16号', 29.5736000, 106.5308000, '023-67859999', '09:30-19:00', NULL, NULL, 4.2, 98, '[\"鸟类\", \"水族\"]', NULL, '专注鸟类和水族诊疗', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (25, '长沙瑞鹏异宠中心', '长沙市芙蓉区五一大道389号', 28.1927000, 113.0038000, '0731-82228888', '09:00-20:00', NULL, NULL, 4.4, 167, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '瑞鹏集团长沙异宠分中心', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (26, '青岛爱诺异宠医院', '青岛市市南区香港中路50号', 36.0648000, 120.3808000, '0532-85719999', '09:00-20:00', NULL, NULL, 4.5, 198, '[\"爬行类\", \"鸟类\", \"水族\", \"两栖类\"]', NULL, '山东地区异宠诊疗标杆', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (27, '大连博爱异宠诊疗', '大连市中山区中山广场2号', 38.9123000, 121.6357000, '0411-82638888', '09:00-19:30', NULL, NULL, 4.3, 145, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '大连专业异宠医院', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (28, '昆明恒佳异宠医院', '昆明市五华区人民中路45号', 25.0438000, 102.7038000, '0871-63629999', '09:00-20:00', NULL, NULL, 4.5, 178, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"两栖类\"]', NULL, '云南异宠诊疗知名品牌，气候适宜各种爬虫诊疗', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (29, '天津瑞派异宠中心', '天津市南开区鞍山西道260号', 39.1347000, 117.1489000, '022-27379999', '09:00-20:00', NULL, NULL, 4.4, 156, '[\"爬行类\", \"鸟类\", \"小型哺乳\", \"水族\"]', NULL, '天津专业异宠诊疗机构', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');
INSERT INTO `hospital` VALUES (30, '郑州瑞鹏异宠医院', '郑州市金水区花园路85号', 34.7602000, 113.6839000, '0371-65998888', '09:00-20:00', NULL, NULL, 4.3, 134, '[\"爬行类\", \"鸟类\", \"小型哺乳\"]', NULL, '郑州异宠专科医院', 1, '2026-07-28 16:20:18', '2026-07-28 16:20:18');

-- ----------------------------
-- Table structure for hospital_appointment
-- ----------------------------
DROP TABLE IF EXISTS `hospital_appointment`;
CREATE TABLE `hospital_appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `hospital_id` bigint NOT NULL COMMENT '医院ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pet_id` bigint NULL DEFAULT NULL COMMENT '宠物ID',
  `appoint_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段(如 09:00-10:00)',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态: 0待确认 1已确认 2已完成 3已取消',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hospital_date`(`hospital_id` ASC, `appoint_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医院预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hospital_appointment
-- ----------------------------
INSERT INTO `hospital_appointment` VALUES (6, 1, 1, NULL, '2026-08-01', '上午 9:00-12:00', '张三', '13800138001', '兔子脚受伤', 1, NULL, '2026-07-28 18:13:08', '2026-07-28 18:13:08');
INSERT INTO `hospital_appointment` VALUES (7, 1, 2, NULL, '2026-08-02', '下午 14:00-18:00', '李四', '13800138002', '', 0, NULL, '2026-07-28 18:13:08', '2026-07-28 18:13:08');
INSERT INTO `hospital_appointment` VALUES (8, 4, 1, NULL, '2026-08-03', '上午 9:00-12:00', '张三', '13800138001', '乌龟体检', 2, NULL, '2026-07-28 18:13:08', '2026-07-28 18:13:08');
INSERT INTO `hospital_appointment` VALUES (9, 6, 6, NULL, '2026-08-05', '晚间 18:00-21:00', '王五', '13800138006', '貂请尽快安排', 1, NULL, '2026-07-28 18:13:08', '2026-07-28 18:13:08');
INSERT INTO `hospital_appointment` VALUES (10, 8, 8, NULL, '2026-08-06', '下午 14:00-18:00', '赵六', '13800138008', '角蛙不吃东西', 0, NULL, '2026-07-28 18:13:08', '2026-07-28 18:13:08');

-- ----------------------------
-- Table structure for hospital_review
-- ----------------------------
DROP TABLE IF EXISTS `hospital_review`;
CREATE TABLE `hospital_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `hospital_id` bigint NOT NULL COMMENT '医院ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `appoint_id` bigint NULL DEFAULT NULL COMMENT '关联预约ID',
  `rating` tinyint(1) NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hospital_id`(`hospital_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医院评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hospital_review
-- ----------------------------
INSERT INTO `hospital_review` VALUES (1, 1, 1, NULL, 5, '医生非常专业,兔子拉肚子看了两次就好了', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (2, 1, 2, NULL, 4, '设备很先进,建议提前预约', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (3, 1, 1, NULL, 5, '仓鼠肿瘤手术做得很成功', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (4, 2, 3, NULL, 4, '看鸟类的医生很专业', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (5, 2, 1, NULL, 5, '医生耐心讲解了很多饲养知识', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (6, 2, 2, NULL, 3, '价格偏高但是医术确实好', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (7, 4, 4, NULL, 5, '上海最好的异宠医院,乌龟肺炎治好了', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (8, 4, 5, NULL, 5, '爬虫专属病房环境控制得很好', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (9, 4, 4, NULL, 4, '预约有点难但是服务确实好', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (10, 6, 6, NULL, 5, '24小时急诊救了急,貂宝宝及时救治', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (11, 6, 7, NULL, 4, '华南异宠诊疗标杆名不虚传', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (12, 8, 8, NULL, 5, 'DR设备很清晰诊断准确', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (13, 8, 9, NULL, 5, '带角蛙去看的,医生很有经验', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (14, 10, 10, NULL, 4, '西南地区最专业的,推荐', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (15, 10, 11, NULL, 5, '龙猫牙齿问题处理得非常好', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (16, 12, 12, NULL, 4, '浙江省内专业异宠诊疗', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (17, 14, 14, NULL, 4, '南京地区专业异宠诊疗', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (18, 16, 16, NULL, 4, '华中异宠医疗值得信赖', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (19, 18, 18, NULL, 4, '西北地区异宠诊疗很专业', '2026-07-28 18:13:08');
INSERT INTO `hospital_review` VALUES (20, 22, 20, NULL, 5, '云南异宠诊疗热带宠物很擅长', '2026-07-28 18:13:08');

-- ----------------------------
-- Table structure for medical_case
-- ----------------------------
DROP TABLE IF EXISTS `medical_case`;
CREATE TABLE `medical_case`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '病例ID',
  `pet_id` bigint NOT NULL COMMENT '宠物ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（冗余，方便按用户查询）',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '病例标题',
  `visit_date` date NOT NULL COMMENT '就诊日期',
  `hospital_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '就诊医院',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主治医生',
  `symptoms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主要症状描述',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '诊断结果',
  `treatment_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '治疗方案',
  `medication` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用药信息',
  `severity` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MILD' COMMENT '严重程度: MILD/MODERATE/SEVERE',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态: 0就诊中 1已康复 2复诊中',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关图片URL（JSON数组）',
  `follow_up_date` date NULL DEFAULT NULL COMMENT '复查日期',
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_visit_date`(`visit_date` ASC) USING BTREE,
  INDEX `idx_severity`(`severity` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '病例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_case
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '目标用户ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: SYSTEM/CONSULT/HEALTH',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_at` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for pet
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物昵称',
  `breed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物大类: 爬行类/鸟类/水族/小型哺乳',
  `breed_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '具体品种',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别 0未知 1雄性 2雌性',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `weight` decimal(7, 2) NULL DEFAULT NULL COMMENT '体重(g)',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `is_current` tinyint(1) NULL DEFAULT 0 COMMENT '是否当前宠物 1是 0否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_breed_type`(`breed_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宠物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet
-- ----------------------------
INSERT INTO `pet` VALUES (1, 1, '小壁', '爬行类', '豹纹守宫', 1, '2024-06-01', 68.00, '/images/爱宠/u1316.svg', 0, '2026-07-27 11:51:19', '2026-07-27 12:05:39');
INSERT INTO `pet` VALUES (2, 1, '龟哥', '爬行类', '鳄龟', 1, '2023-04-15', 475.00, '/images/爱宠/u1315.svg', 0, '2026-07-27 11:51:19', '2026-07-27 12:05:39');
INSERT INTO `pet` VALUES (3, 1, '小鹉', '鸟类', '牡丹鹦鹉', 2, '2024-08-20', 68.00, '/images/爱宠/u1309.svg', 0, '2026-07-27 11:51:19', '2026-07-27 12:05:39');

-- ----------------------------
-- Table structure for reminder_plan
-- ----------------------------
DROP TABLE IF EXISTS `reminder_plan`;
CREATE TABLE `reminder_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `pet_id` bigint NOT NULL COMMENT '宠物ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `remind_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提醒类型: vaccine/deworm/checkup/medication',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提醒标题',
  `remind_date` date NOT NULL COMMENT '提醒日期',
  `repeat_type` tinyint(1) NULL DEFAULT 0 COMMENT '重复类型: 0单次 1每周 2每月 3每季度 4每年',
  `repeat_interval` int NULL DEFAULT NULL COMMENT '自定义间隔天数',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态: 0待处理 1已完成 2已过期',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_remind_date`(`remind_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提醒计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reminder_plan
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码(bcrypt加密)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '18845058230', NULL, '用户8230', NULL, 0, 1, NULL, '2026-07-28 23:50:01', '2026-07-28 23:50:01');
INSERT INTO `user` VALUES (2, '13800138000', NULL, '用户8000', NULL, 0, 1, NULL, '2026-07-29 11:17:27', '2026-07-29 11:17:27');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认地址 1是 0否',
  `label` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签(家/公司)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_default`(`user_id` ASC, `is_default` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
