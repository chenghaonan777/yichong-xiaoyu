package com.exopet.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康档案记录表
 */
@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    /**
     * 记录类型: vaccine(疫苗) / deworm(驱虫) / checkup(体检) /
     *           medication(用药) / weight(体重) / consult(问诊)
     */
    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /** 下次日期，用于到期提醒 */
    private LocalDate nextDate;

    private String doctorName;

    private String notes;

    /** 关联业务ID（如问诊ID） */
    private Long relatedId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
