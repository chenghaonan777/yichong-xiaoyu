package com.exopet.hospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author 23278
 * @Date 2026/7/27 17:39
 * @PackageName:com.exopet.hospital.entity
 * @ClassName:HospitalAppointment
 * @Description: TODO
 * @Version 1.0
 */
@Data
@TableName("hospital_appointment")
public class HospitalAppointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull(message = "医院ID不能为空")
    private Long hospitalId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /** 宠物ID */
    private Long petId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointDate;

    @NotBlank(message = "时间段不能为空")
    private String timeSlot;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 备注 */
    private String remark;

    /**
     * 状态: 0待确认 1已确认 2已完成 3已取消
     */
    private Integer status;

    /** 取消原因 */
    private String cancelReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;


}