-- ============================================================
-- 异宠小愈 (ExoPet) 数据库初始化脚本
-- 版本: V1.0
-- 数据库: exopet (单库多模块)
-- 引擎: InnoDB
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `exopet` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `exopet`;

-- ============================================================
-- 模块一: 用户服务 (module_user)
-- ============================================================

-- 1.1 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`              bigint(20)    NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `phone`           varchar(11)   NOT NULL                 COMMENT '手机号',
    `password`        varchar(255)  DEFAULT NULL             COMMENT '密码(bcrypt加密)',
    `nickname`        varchar(50)   DEFAULT NULL             COMMENT '昵称',
    `avatar`          varchar(500)  DEFAULT NULL             COMMENT '头像URL',
    `gender`          tinyint(1)    DEFAULT 0                COMMENT '性别 0未知 1男 2女',
    `status`          tinyint(1)    DEFAULT 1                COMMENT '状态 1正常 0禁用',
    `last_login_time` datetime      DEFAULT NULL             COMMENT '最后登录时间',
    `created_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP     COMMENT '创建时间',
    `updated_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1.2 收货地址表
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
    `id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`      bigint(20)    NOT NULL                COMMENT '用户ID',
    `name`         varchar(50)   NOT NULL                COMMENT '收货人姓名',
    `phone`        varchar(11)   NOT NULL                COMMENT '联系电话',
    `province`     varchar(50)   DEFAULT NULL            COMMENT '省',
    `city`         varchar(50)   DEFAULT NULL            COMMENT '市',
    `district`     varchar(50)   DEFAULT NULL            COMMENT '区',
    `detail`       varchar(200)  NOT NULL                COMMENT '详细地址',
    `is_default`   tinyint(1)    DEFAULT 0               COMMENT '是否默认地址 1是 0否',
    `label`        varchar(20)   DEFAULT NULL            COMMENT '标签(家/公司)',
    `created_at`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_default` (`user_id`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';


-- ============================================================
-- 模块四: 问诊服务 (module_consult)
-- ============================================================

-- 4.1 医生表
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '医生ID',
    `name`           varchar(50)  NOT NULL                COMMENT '姓名',
    `title`          varchar(50)  DEFAULT NULL            COMMENT '职称(执业兽医师/主治兽医师)',
    `avatar`         varchar(500) DEFAULT NULL            COMMENT '头像URL',
    `cert_no`        varchar(50)  DEFAULT NULL            COMMENT '执业证书编号',
    `cert_image`     varchar(500) DEFAULT NULL            COMMENT '证书图片URL',
    `years_exp`      int(11)      DEFAULT 0              COMMENT '从业年限',
    `expertise_tags` json                                 COMMENT '擅长品类标签(爬行类/鸟类/水族/小型哺乳)',
    `intro`          varchar(500) DEFAULT NULL            COMMENT '个人简介',
    `rating`         decimal(2,1) DEFAULT 5.0            COMMENT '综合评分',
    `consult_count`  int(11)      DEFAULT 0              COMMENT '累计接诊数',
    `price_text`     decimal(10,2) DEFAULT 0.00          COMMENT '图文问诊价格',
    `price_video`    decimal(10,2) DEFAULT 0.00          COMMENT '视频问诊价格',
    `hospital_name`  varchar(100) DEFAULT NULL            COMMENT '所属医院',
    `online_status`  tinyint(1)   DEFAULT 0              COMMENT '在线状态 0离线 1在线 2忙碌',
    `status`         tinyint(1)   DEFAULT 1              COMMENT '状态 1启用 0停用',
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_rating` (`rating`),
    KEY `idx_online_status` (`online_status`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- 4.2 问诊订单表
DROP TABLE IF EXISTS `consult_order`;
CREATE TABLE `consult_order` (
    `id`              bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '问诊单ID',
    `order_no`        varchar(32)   NOT NULL                COMMENT '问诊单号(业务唯一)',
    `user_id`         bigint(20)    NOT NULL                COMMENT '用户ID',
    `pet_id`          bigint(20)    DEFAULT NULL            COMMENT '宠物ID',
    `doctor_id`       bigint(20)    DEFAULT NULL            COMMENT '医生ID(AI问诊为空)',
    `type`            tinyint(1)    NOT NULL                COMMENT '问诊类型: 1AI问诊 2真人图文 3真人视频 4急诊',
    `status`          tinyint(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0待支付 1待接诊 2问诊中 3已完成 4已取消',
    `amount`          decimal(10,2) NOT NULL                COMMENT '金额',
    `symptom_desc`    varchar(500)  DEFAULT NULL            COMMENT '症状文字描述',
    `symptom_images`  json                                  COMMENT '症状图片URL数组',
    `breed_type`      varchar(50)   DEFAULT NULL            COMMENT '宠物大类(爬行类/鸟类/...)',
    `breed_name`      varchar(100)  DEFAULT NULL            COMMENT '宠物品种名称',
    `diagnosis_result` json         DEFAULT NULL            COMMENT '诊断结果(JSON: 疾病列表+养护方案等)',
    `ai_consult_id`   bigint(20)    DEFAULT NULL            COMMENT '关联AI诊断ID(AI问诊转真人时使用)',
    `paid_at`         datetime      DEFAULT NULL            COMMENT '支付时间',
    `finished_at`     datetime      DEFAULT NULL            COMMENT '完成时间',
    `created_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_doctor_id` (`doctor_id`),
    KEY `idx_doctor_status` (`doctor_id`, `status`),
    KEY `idx_type` (`type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊订单表';

-- 4.3 问诊消息表 (WebSocket 聊天记录)
DROP TABLE IF EXISTS `consult_message`;
CREATE TABLE `consult_message` (
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `consult_id`  bigint(20)   NOT NULL                COMMENT '问诊订单ID',
    `sender_type` tinyint(1)   NOT NULL                COMMENT '发送者类型: 1用户 2医生 3系统',
    `sender_id`   bigint(20)   DEFAULT NULL            COMMENT '发送者用户/医生ID',
    `msg_type`    tinyint(1)   DEFAULT 1               COMMENT '消息类型: 1文字 2图片 3语音 4系统提示 5处方卡片',
    `content`     text                                 COMMENT '消息内容',
    `created_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_consult_id` (`consult_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊消息表';

-- 4.4 用户评价医生表
DROP TABLE IF EXISTS `doctor_review`;
CREATE TABLE `doctor_review` (
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `consult_id`    bigint(20)   NOT NULL                COMMENT '问诊订单ID',
    `user_id`       bigint(20)   NOT NULL                COMMENT '用户ID',
    `doctor_id`     bigint(20)   NOT NULL                COMMENT '医生ID',
    `rating`        tinyint(1)   NOT NULL DEFAULT 5      COMMENT '评分 1-5星',
    `content`       varchar(500) DEFAULT NULL            COMMENT '评价内容',
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_consult` (`consult_id`),
    KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生评价表';


-- ============================================================
-- 模块五: AI诊断服务 (module_ai)
-- ============================================================

-- 5.1 AI诊断记录表
DROP TABLE IF EXISTS `ai_diagnosis_record`;
CREATE TABLE `ai_diagnosis_record` (
    `id`              bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '诊断记录ID',
    `user_id`         bigint(20)   NOT NULL                COMMENT '用户ID',
    `consult_id`      bigint(20)   DEFAULT NULL            COMMENT '关联问诊订单ID',
    `breed_type`      varchar(50)  DEFAULT NULL            COMMENT '宠物大类',
    `breed_name`      varchar(100) DEFAULT NULL            COMMENT '品种名称',
    `symptoms`        json                                 COMMENT '用户勾选症状标签数组',
    `symptom_desc`    varchar(500) DEFAULT NULL            COMMENT '用户补充文字描述',
    `images`          json                                 COMMENT '上传图片URL数组',
    `ai_model`        varchar(50)  DEFAULT NULL            COMMENT '使用的AI模型(qwen/gpt/ollama)',
    `ai_raw_response` json                                 COMMENT 'AI原始响应(全量)',
    `disease_list`    json                                 COMMENT '诊断结果: 疾病列表[{name,probability,severity,detail}]',
    `care_plan`       text                                 COMMENT '养护方案(纯文本)',
    `confidence`      decimal(3,2) DEFAULT NULL            COMMENT '整体置信度',
    `user_feedback`   tinyint(1)   DEFAULT NULL            COMMENT '用户反馈: 1有用 0无用',
    `duration_ms`     int(11)      DEFAULT NULL            COMMENT 'AI诊断耗时(毫秒)',
    `created_at`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_consult_id` (`consult_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI诊断记录表';


-- ============================================================
-- 模块六: 宠物服务 (module_pet)
-- ============================================================

-- 6.1 宠物表
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
    `id`          bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    `user_id`     bigint(20)    NOT NULL                COMMENT '用户ID',
    `name`        varchar(50)   NOT NULL                COMMENT '宠物昵称',
    `breed_type`  varchar(50)   NOT NULL                COMMENT '宠物大类: 爬行类/鸟类/水族/小型哺乳',
    `breed_name`  varchar(100)  NOT NULL                COMMENT '具体品种',
    `gender`      tinyint(1)    DEFAULT 0               COMMENT '性别 0未知 1雄性 2雌性',
    `birthday`    date          DEFAULT NULL            COMMENT '生日',
    `weight`      decimal(7,2)  DEFAULT NULL            COMMENT '体重(g)',
    `avatar`      varchar(500)  DEFAULT NULL            COMMENT '头像URL',
    `is_current`  tinyint(1)    DEFAULT 0               COMMENT '是否当前宠物 1是 0否',
    `created_at`  datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_breed_type` (`breed_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物表';

-- 6.2 健康档案记录表
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `pet_id`        bigint(20)   NOT NULL                COMMENT '宠物ID',
    `record_type`   varchar(30)  NOT NULL                COMMENT '记录类型: vaccine/deworm/checkup/medication/weight/consult',
    `title`         varchar(100) NOT NULL                COMMENT '标题(如: 狂犬疫苗第一针)',
    `record_date`   date         NOT NULL                COMMENT '记录日期',
    `next_date`     date         DEFAULT NULL            COMMENT '下次日期(用于到期提醒)',
    `doctor_name`   varchar(50)  DEFAULT NULL            COMMENT '操作兽医姓名',
    `notes`         varchar(500) DEFAULT NULL            COMMENT '备注',
    `related_id`    bigint(20)   DEFAULT NULL            COMMENT '关联业务ID(如问诊ID)',
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_pet_id` (`pet_id`),
    KEY `idx_record_type` (`record_type`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_next_date` (`next_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案记录表';

-- 6.3 提醒计划表
DROP TABLE IF EXISTS `reminder_plan`;
CREATE TABLE `reminder_plan` (
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    `pet_id`        bigint(20)   NOT NULL                COMMENT '宠物ID',
    `user_id`       bigint(20)   NOT NULL                COMMENT '用户ID',
    `remind_type`   varchar(30)  NOT NULL                COMMENT '提醒类型: vaccine/deworm/checkup/medication',
    `title`         varchar(100) NOT NULL                COMMENT '提醒标题',
    `remind_date`   date         NOT NULL                COMMENT '提醒日期',
    `repeat_type`   tinyint(1)   DEFAULT 0               COMMENT '重复类型: 0单次 1每周 2每月 3每季度 4每年',
    `repeat_interval` int(11)    DEFAULT NULL            COMMENT '自定义间隔天数',
    `status`        tinyint(1)   DEFAULT 0               COMMENT '状态: 0待处理 1已完成 2已过期',
    `remark`        varchar(200) DEFAULT NULL            COMMENT '备注',
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_pet_id` (`pet_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_remind_date` (`remind_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒计划表';

-- 6.4 病例表
DROP TABLE IF EXISTS `medical_case`;
CREATE TABLE `medical_case` (
    `id`             bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '病例ID',
    `pet_id`         bigint(20)    NOT NULL                COMMENT '宠物ID',
    `user_id`        bigint(20)    NOT NULL                COMMENT '用户ID（冗余，方便按用户查询）',
    `title`          varchar(200)  NOT NULL                COMMENT '病例标题',
    `visit_date`     date          NOT NULL                COMMENT '就诊日期',
    `hospital_name`  varchar(100)  DEFAULT NULL            COMMENT '就诊医院',
    `doctor_name`    varchar(50)   DEFAULT NULL            COMMENT '主治医生',
    `symptoms`       varchar(500)  DEFAULT NULL            COMMENT '主要症状描述',
    `diagnosis`      text                                  COMMENT '诊断结果',
    `treatment_plan` text                                  COMMENT '治疗方案',
    `medication`     varchar(500)  DEFAULT NULL            COMMENT '用药信息',
    `severity`       varchar(10)   DEFAULT 'MILD'          COMMENT '严重程度: MILD/MODERATE/SEVERE',
    `status`         tinyint(1)    DEFAULT 0               COMMENT '状态: 0就诊中 1已康复 2复诊中',
    `images`         varchar(1000) DEFAULT NULL            COMMENT '相关图片URL（JSON数组）',
    `follow_up_date` date          DEFAULT NULL            COMMENT '复查日期',
    `notes`          varchar(500)  DEFAULT NULL            COMMENT '备注',
    `created_at`     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_pet_id` (`pet_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_visit_date` (`visit_date`),
    KEY `idx_severity` (`severity`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病例表';


-- ============================================================
-- 模块七: 医院服务 (module_hospital)
-- ============================================================

-- 7.1 医院表
DROP TABLE IF EXISTS `hospital`;
CREATE TABLE `hospital` (
    `id`              bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '医院ID',
    `name`            varchar(100)  NOT NULL                COMMENT '医院名称',
    `address`         varchar(200)  NOT NULL                COMMENT '详细地址',
    `latitude`        decimal(10,7) DEFAULT NULL            COMMENT '纬度(地图标注)',
    `longitude`       decimal(10,7) DEFAULT NULL            COMMENT '经度(地图标注)',
    `phone`           varchar(20)   DEFAULT NULL            COMMENT '联系电话',
    `business_hours`  varchar(100)  DEFAULT NULL            COMMENT '营业时间(如 09:00-21:00)',
    `cover_image`     varchar(500)  DEFAULT NULL            COMMENT '封面图URL',
    `images`          json                                  COMMENT '环境图URL数组',
    `rating`          decimal(2,1)  DEFAULT 5.0            COMMENT '综合评分',
    `review_count`    int(11)       DEFAULT 0               COMMENT '评价数',
    `expertise_tags`  json                                  COMMENT '接诊品类标签数组',
    `license_image`   varchar(500)  DEFAULT NULL            COMMENT '执业许可证照片URL',
    `intro`           text                                  COMMENT '医院简介',
    `status`          tinyint(1)    DEFAULT 1               COMMENT '状态 1启用 0停用',
    `created_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_rating` (`rating`),
    KEY `idx_lat_lng` (`latitude`, `longitude`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院表';

-- 7.2 医院预约表
DROP TABLE IF EXISTS `hospital_appointment`;
CREATE TABLE `hospital_appointment` (
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `hospital_id`   bigint(20)   NOT NULL                COMMENT '医院ID',
    `user_id`       bigint(20)   NOT NULL                COMMENT '用户ID',
    `pet_id`        bigint(20)   DEFAULT NULL            COMMENT '宠物ID',
    `appoint_date`  date         NOT NULL                COMMENT '预约日期',
    `time_slot`     varchar(30)  NOT NULL                COMMENT '时间段(如 09:00-10:00)',
    `contact_name`  varchar(50)  DEFAULT NULL            COMMENT '联系人',
    `contact_phone` varchar(11)  DEFAULT NULL            COMMENT '联系电话',
    `remark`        varchar(200) DEFAULT NULL            COMMENT '备注',
    `status`        tinyint(1)   DEFAULT 0               COMMENT '状态: 0待确认 1已确认 2已完成 3已取消',
    `cancel_reason` varchar(200) DEFAULT NULL            COMMENT '取消原因',
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_hospital_date` (`hospital_id`, `appoint_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院预约表';

-- 7.3 医院评价表
DROP TABLE IF EXISTS `hospital_review`;
CREATE TABLE `hospital_review` (
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `hospital_id` bigint(20)   NOT NULL                COMMENT '医院ID',
    `user_id`     bigint(20)   NOT NULL                COMMENT '用户ID',
    `appoint_id`  bigint(20)   DEFAULT NULL            COMMENT '关联预约ID',
    `rating`      tinyint(1)   NOT NULL DEFAULT 5      COMMENT '评分 1-5星',
    `content`     varchar(500) DEFAULT NULL            COMMENT '评价内容',
    `created_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_hospital_id` (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院评价表';


-- ============================================================
-- 初始化数据
-- ============================================================

-- 医生初始化
INSERT INTO `doctor` (`id`, `name`, `title`, `years_exp`, `expertise_tags`, `intro`, `rating`, `consult_count`, `price_text`, `price_video`, `hospital_name`, `online_status`, `status`) VALUES
(1, '程浩男', '主治兽医', 10, '["爬行类","鸟类"]',  '异宠诊疗10年经验，擅长爬行动物疾病诊治',  4.9, 1200, 29.90, 59.90, '爱诺异宠医院', 1, 1),
(2, '王贵暄', '主治兽医', 8,  '["鸟类","小型哺乳"]', '异宠诊疗8年经验，擅长鸟类和小型哺乳动物',  4.8, 980,  29.90, 59.90, '宠颐生异宠诊疗中心', 1, 1);

-- 医院初始化
INSERT INTO `hospital` (`id`, `name`, `address`, `latitude`, `longitude`, `phone`, `business_hours`, `rating`, `expertise_tags`, `status`) VALUES
(1, '爱诺异宠医院',     '北京市朝阳区建国路88号',  39.9087, 116.4716, '010-88886666', '09:00-21:00', 4.8, '["爬行类","鸟类","水族","小型哺乳"]', 1),
(2, '宠颐生异宠诊疗中心', '北京市海淀区中关村大街66号', 39.9836, 116.3218, '010-66668888', '09:00-20:00', 4.6, '["爬行类","鸟类","小型哺乳"]', 1),
(3, '瑞鹏宠物医院',     '北京市东城区东直门外大街42号', 39.9352, 116.4344, '010-55557777', '08:00-22:00', 4.5, '["鸟类","水族","小型哺乳"]', 1);
