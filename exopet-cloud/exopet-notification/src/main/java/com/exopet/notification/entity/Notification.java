package com.exopet.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author 23278
 * @Date 2026/7/29 18:48
 * @PackageName:com.exopet.notification.entity
 * @ClassName:Notification
 * @Description: TODO
 * @Version 1.0
 */
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long relatedId;

    private Integer isRead;
    private LocalDateTime readAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}