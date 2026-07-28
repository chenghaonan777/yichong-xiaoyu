package com.exopet.consult.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consult_message")
public class ConsultMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "问诊订单ID不能为空")
    private Long consultId;

    /** 发送者类型: 1用户 2医生 3系统 */
    @NotNull(message = "发送者类型不能为空")
    private Integer senderType;

    private Long senderId;

    /** 消息类型: 1文字 2图片 3语音 4系统提示 5处方卡片 */
    private Integer msgType;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
