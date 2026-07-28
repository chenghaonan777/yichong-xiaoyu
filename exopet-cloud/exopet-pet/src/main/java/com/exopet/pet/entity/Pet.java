package com.exopet.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物表
 */
@Data
@TableName("pet")
public class Pet {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "宠物昵称不能为空")
    private String name;

    @NotBlank(message = "宠物大类不能为空")
    private String breedType;

    @NotBlank(message = "品种名称不能为空")
    private String breedName;

    /** 性别: 0未知 1雄性 2雌性 */
    private Integer gender;

    private LocalDate birthday;

    /** 体重(克) */
    private BigDecimal weight;

    private String avatar;

    /** 是否当前宠物 */
    private Integer isCurrent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
