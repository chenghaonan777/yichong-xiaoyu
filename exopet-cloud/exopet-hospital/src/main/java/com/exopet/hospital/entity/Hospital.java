package com.exopet.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author 23278
 * @Date 2026/7/27 17:38
 * @PackageName:com.exopet.hospital.entity
 * @ClassName:Hospital
 * @Description: TODO
 * @Version 1.0
 */
@Data
@TableName
public class Hospital {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "医院名称不能为空")
    private String name;

    @NotBlank(message = "地址不能为空")
    private String address;

//    纬度
    private BigDecimal latitude;
//    经度
    private BigDecimal longitude;
    private String phone;
    /** 营业时间(如 09:00-21:00) */
    private String businessHours;
    /** 封面图URL */
    private String coverImage;
    /** 环境图URL数组(JSON) */
    private String images;
    /** 综合评分 */
    private  BigDecimal rating;
    /** 评价数 */
    private Integer reviewCount;
    /** 接诊品类标签(JSON数组) */
    private String expertiseTags;
    /** 执业许可证照片URL */
    private String licenseImage;
    /** 医院简介 */
    private String intro;

    /** 状态: 1启用 0停用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}