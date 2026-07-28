package com.exopet.consult.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doctor_review")
public class DoctorReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "问诊订单ID不能为空")
    private Long consultId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    /** 评分 1-5星 */
    @NotNull(message = "评分不能为空")
    private Integer rating;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
