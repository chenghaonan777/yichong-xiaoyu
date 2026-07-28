package com.exopet.consult.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("doctor")
public class Doctor {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "医生姓名不能为空")
    private String name;

    private String title;
    private String avatar;
    private String certNo;
    private String certImage;
    private Integer yearsExp;

    /** 擅长品类标签: ["爬行类","鸟类"] */
    private String expertiseTags;

    private String intro;

    /** 综合评分 5.0 */
    private BigDecimal rating;

    private Integer consultCount;

    /** 图文问诊价格 */
    private BigDecimal priceText;

    /** 视频问诊价格 */
    private BigDecimal priceVideo;

    private String hospitalName;

    /** 在线状态: 0离线 1在线 2忙碌 */
    private Integer onlineStatus;

    /** 状态: 1启用 0停用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
