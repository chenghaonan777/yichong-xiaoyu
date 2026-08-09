package com.exopet.notification.dto;

import lombok.Data;

/**
 * @Author 23278
 * @Date 2026/7/30 13:15
 * @PackageName:com.exopet.notification.dto
 * @ClassName:NotificationMessage
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class NotificationMessage {
private Long userId;
private String type;     //SYStem /CONSULT/health
private String title;
private String content;
private Long relatedId;


}