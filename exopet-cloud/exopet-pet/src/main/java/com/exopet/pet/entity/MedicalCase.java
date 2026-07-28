package com.exopet.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病例表 — 宠物就诊病历记录
 */
@Data
@TableName("medical_case")
public class MedicalCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联宠物ID */
    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    /** 所属用户ID（冗余，方便按用户查询） */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 病例标题 */
    @NotBlank(message = "病例标题不能为空")
    private String title;

    /** 就诊日期 */
    @NotNull(message = "就诊日期不能为空")
    private LocalDate visitDate;

    /** 就诊医院 */
    private String hospitalName;

    /** 主治医生 */
    private String doctorName;

    /** 主要症状描述 */
    private String symptoms;

    /** 诊断结果 */
    private String diagnosis;

    /** 治疗方案 */
    private String treatmentPlan;

    /** 用药信息 */
    private String medication;

    /**
     * 严重程度：MILD / MODERATE / SEVERE
     */
    private String severity;

    /**
     * 状态：0-就诊中  1-已康复  2-复诊中
     */
    private Integer status;

    /** 相关图片URL（JSON数组） */
    private String images;

    /** 复查日期 */
    private LocalDate followUpDate;

    /** 备注 */
    private String notes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
