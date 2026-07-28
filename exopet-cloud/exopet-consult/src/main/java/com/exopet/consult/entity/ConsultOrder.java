package com.exopet.consult.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consult_order")
public class ConsultOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "问诊单号不能为空")
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long petId;
    private Long doctorId;

    /** 问诊类型: 1AI问诊 2真人图文 3真人视频 4急诊 */
    @NotNull(message = "问诊类型不能为空")
    private Integer type;

    /** 0待支付 1待接诊 2问诊中 3已完成 4已取消 */
    private Integer status;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String symptomDesc;

    /** 症状图片URL数组 */
    private String symptomImages;

    private String breedType;
    private String breedName;

    /** 诊断结果JSON */
    private String diagnosisResult;

    private Long aiConsultId;
    private LocalDateTime paidAt;
    private LocalDateTime finishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
