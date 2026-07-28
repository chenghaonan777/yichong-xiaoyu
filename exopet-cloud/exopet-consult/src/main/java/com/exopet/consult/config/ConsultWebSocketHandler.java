package com.exopet.consult.config;

import com.exopet.consult.entity.ConsultMessage;
import com.exopet.consult.mapper.ConsultMessageMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConsultWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ConsultWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConsultMessageMapper consultMessageMapper;
    private final ObjectMapper objectMapper;

    public ConsultWebSocketHandler(ConsultMessageMapper consultMessageMapper, ObjectMapper objectMapper) {
        this.consultMessageMapper = consultMessageMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket 连接: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析消息
        Map msg = objectMapper.readValue(message.getPayload(), Map.class);
        Long consultId = Long.valueOf(msg.get("consultId").toString());
        String content = msg.get("content").toString();

        // 存库
        ConsultMessage userMsg = new ConsultMessage();
        userMsg.setConsultId(consultId);
        userMsg.setSenderType(1);
        userMsg.setMsgType(1);
        userMsg.setContent(content);
        consultMessageMapper.insert(userMsg);

        // 推送给所有连接同一问诊单的客户端
        String userMsgJson = objectMapper.writeValueAsString(userMsg);
        broadcastToConsult(consultId, userMsgJson);

        // 模拟医生自动回复（2秒后）
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                ConsultMessage doctorMsg = new ConsultMessage();
                doctorMsg.setConsultId(consultId);
                doctorMsg.setSenderType(2);
                doctorMsg.setMsgType(1);
                doctorMsg.setContent("收到您的消息，请稍等，我正在查看您宠物的症状...");
                consultMessageMapper.insert(doctorMsg);

                String doctorMsgJson = objectMapper.writeValueAsString(doctorMsg);
                broadcastToConsult(consultId, doctorMsgJson);
            } catch (Exception e) {
                log.error("模拟医生回复失败", e);
            }
        }).start();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket 断开: {}", session.getId());
    }

    private void broadcastToConsult(Long consultId, String json) {
        sessions.values().forEach(s -> {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    log.error("推送失败", e);
                }
            }
        });
    }
}
