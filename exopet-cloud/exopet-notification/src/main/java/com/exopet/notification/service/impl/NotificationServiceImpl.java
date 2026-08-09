package com.exopet.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.notification.entity.Notification;
import com.exopet.notification.mapper.NotificationMapper;
import com.exopet.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * @Author 23278
 * @Date 2026/7/29 22:08
 * @PackageName:com.exopet.notification.service.Impl
 * @ClassName:NotificationServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Override
    public IPage<Notification> listByUserId(Long userId, int page, int size, Boolean isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead ? 1 : 0);
        }
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public long countUnread(Long userId) {
        return count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        lambdaUpdate()
                .eq(Notification::getId, id)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadAt, LocalDateTime.now())
                .update();
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        lambdaUpdate()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadAt, LocalDateTime.now())
                .update();
    }

    @Override
    public Notification createNotification(Long userId, String type, String title, String content, Long relatedId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setRelatedId(relatedId);
        n.setIsRead(0);
        save(n);
        return n;
    }
}