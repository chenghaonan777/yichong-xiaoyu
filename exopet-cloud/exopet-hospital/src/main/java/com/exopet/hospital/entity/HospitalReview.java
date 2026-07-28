package com.exopet.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author 23278
 * @Date 2026/7/27 17:39
 * @PackageName:com.exopet.hospital.entity
 * @ClassName:HospitalReview
 * @Description: TODO
 * @Version 1.0
 */
@Data
@TableName("hospital_review")
public class HospitalReview {
  @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull(message = "医院ID不能为空")
    private Long hospitalId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /** 关联预约ID */
    private Long appointId;

    /** 评分 1-5星 */
    @NotNull(message = "评分不能为空")
    private Integer rating;

    /** 评价内容 */
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}