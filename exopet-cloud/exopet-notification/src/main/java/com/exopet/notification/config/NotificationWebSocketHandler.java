package com.exopet.notification.config;

import com.exopet.notification.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 23278
 * @Date 2026/7/30 14:04
 * @PackageName:com.exopet.notification.config
 * @ClassName:NotificationWebSocketHandler
 * @Description: TODO
 * @Version 1.0
 */
@Slf4j
@Component
public class NotificationWebSocketHandler  extends TextWebSocketHandler {
    /** userId -> WebSocketSession */
    private final Map<Long, WebSocketSession>  sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        String query = session.getUri() != null ? session.getUri().getQuery() : "";
        // 从query取userid 如 ws：//。。/ws/notification？userid=1
        String userIdStr =query.replace("userId=","");
        try{
            Long userId = Long.parseLong(userIdStr);
            sessions.put(userId, session);
            log.info("通知WebSocket已连接: userId={}", userId);
        } catch (NumberFormatException e){
            log.warn("通知WebSocket连接缺少有效userId: {}", query);
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("通知WebSocket已断开");
    }
    /** 向指定用户推送通知 */
    public void sendToUser(Long userId, Notification notification) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(notification);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("推送通知给userId={}失败", userId, e);
            }
        }
    }
}