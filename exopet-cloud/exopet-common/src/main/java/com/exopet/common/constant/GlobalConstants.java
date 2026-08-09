package com.exopet.common.constant;

/**
 * 全局常量
 */
public interface GlobalConstants {

    String TOKEN_PREFIX = "Bearer ";
    String TOKEN_HEADER = "Authorization";

    String USER_ID_KEY = "userId";
    String USER_INFO_KEY = "userInfo";

    // Redis Key 前缀
    String REDIS_TOKEN_KEY = "exopet:token:";
    String REDIS_DOCTOR_KEY = "exopet:doctor:";
    String REDIS_CASE_KEY = "exopet:pet:case:";

    // RocketMQ Topic（替代原 Kafka）
    String TOPIC_NOTIFICATION_PUSH = "notification-push";
    String TOPIC_CONSULT_REMIND = "consult-remind";
    String TOPIC_PET_HEALTH_REMIND = "pet-health-remind";
}
