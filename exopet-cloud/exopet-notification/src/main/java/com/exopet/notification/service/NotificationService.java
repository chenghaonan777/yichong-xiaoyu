package com.exopet.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.notification.entity.Notification;

/**
 * @Author 23278
 * @Date 2026/7/29 22:05
 * @PackageName:com.exopet.notification.service
 * @ClassName:NotificationService
 * @Description: TODO
 * @Version 1.0
 */
public interface NotificationService   extends IService<Notification> {
    /** 分页查询用户通知 */
    IPage<Notification> listByUserId(Long userId, int page, int size, Boolean isRead);
    /** 未读通知数 */
    long countUnread(Long userId);
    /** 标记已读 */
    void markAsRead(Long id, Long userId);
    /** 全部标记已读 */
    void markAllAsRead(Long userId);

    /** 创建通知并返回 */
    Notification createNotification(Long userId, String type, String title, String content, Long relatedId);
}