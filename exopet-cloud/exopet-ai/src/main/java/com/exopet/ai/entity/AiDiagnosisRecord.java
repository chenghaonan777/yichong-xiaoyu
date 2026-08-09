package com.exopet.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_diagnosis_record")
public class AiDiagnosisRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long consultId;
    private String breedType;
    private String breedName;
    private String symptoms;
    private String symptomDesc;
    private String images;
    private String aiModel;
    private String aiRawResponse;
    private String diseaseList;
    private String carePlan;
    private BigDecimal confidence;
    private Boolean userFeedback;
    private Integer durationMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
