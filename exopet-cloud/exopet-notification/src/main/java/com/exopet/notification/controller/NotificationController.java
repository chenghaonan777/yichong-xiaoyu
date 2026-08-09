package com.exopet.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.notification.config.NotificationWebSocketHandler;
import com.exopet.notification.entity.Notification;
import com.exopet.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "通知管理")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationWebSocketHandler webSocketHandler;

    @Operation(summary = "分页查询用户通知")
    @GetMapping("/list/{userId}")
    public Result<IPage<Notification>> list(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean isRead) {
        return Result.success(notificationService.listByUserId(userId, page, size, isRead));
    }

    @Operation(summary = "未读通知数")
    @GetMapping("/unread/{userId}")
    public Result<Map<String, Long>> unread(@PathVariable Long userId) {
        return Result.success(Map.of("count", notificationService.countUnread(userId)));
    }

    @Operation(summary = "标记单条已读")
    @PutMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
        return Result.success(null);
    }

    @Operation(summary = "全部标记已读")
    @PutMapping("/read-all/{userId}")
    public Result<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success(null);
    }

    @Operation(summary = "生成测试通知（前端调试用）")
    @PostMapping("/test/{userId}")
    public Result<Notification> createTest(@PathVariable Long userId) {
        String[] titles = {"问诊提醒", "健康提醒", "系统通知"};
        String[] contents = {
                "您的问诊单 #1001 医生已接诊，请前往查看",
                "小壁的驱虫到期提醒，请及时安排",
                "欢迎使用异宠小愈，您的专属异宠健康管家"
        };
        String[] types = {"CONSULT", "HEALTH", "SYSTEM"};

        int idx = (int) (System.currentTimeMillis() % 3);
        Notification notification = notificationService.createNotification(
                userId, types[idx], titles[idx], contents[idx], null);
        // WebSocket 实时推送
        webSocketHandler.sendToUser(userId, notification);
        return Result.success(notification);
    }
}
