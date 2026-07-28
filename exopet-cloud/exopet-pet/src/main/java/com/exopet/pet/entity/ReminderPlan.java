package com.exopet.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 提醒计划表
 */
@Data
@TableName("reminder_plan")
public class ReminderPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 提醒类型: vaccine / deworm / checkup / medication */
    @NotBlank(message = "提醒类型不能为空")
    private String remindType;

    @NotBlank(message = "提醒标题不能为空")
    private String title;

    @NotNull(message = "提醒日期不能为空")
    private LocalDate remindDate;

    /** 重复类型: 0单次 1每周 2每月 3每季度 4每年 */
    private Integer repeatType;

    /** 自定义间隔天数 */
    private Integer repeatInterval;

    /** 状态: 0待处理 1已完成 2已过期 */
    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
