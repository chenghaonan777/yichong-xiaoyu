package com.exopet.notification.consumer;

import com.exopet.common.constant.GlobalConstants;
import com.exopet.notification.dto.NotificationMessage;
import com.exopet.notification.entity.Notification;
import com.exopet.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import com.exopet.notification.config.NotificationWebSocketHandler;
/**
 * @Author 23278
 * @Date 2026/7/30 13:18
 * @PackageName:com.exopet.notification.consumer
 * @ClassName:NotificationPushConsumer
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = GlobalConstants.TOPIC_NOTIFICATION_PUSH,
        consumerGroup = "exopet-notification-push-group"
)
public class NotificationPushConsumer implements RocketMQListener<String> {
   private final NotificationService notificationService;
   private final NotificationWebSocketHandler webSocketHandler;
   private final ObjectMapper objectMapper = new ObjectMapper();

   @Override
    public void onMessage(String message){
       log.info("收到通知推送消息：{}",message);
       try {
           NotificationMessage msg = objectMapper.readValue(message, NotificationMessage.class);
           Notification notification = notificationService.createNotification(
                   msg.getUserId(), msg.getType(), msg.getTitle(), msg.getContent(), msg.getRelatedId());
           // WebSocket 推送
           webSocketHandler.sendToUser(msg.getUserId(), notification);
       } catch (Exception e) {
           log.error("处理通知推送消息失败", e);
       }
   }
}