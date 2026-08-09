package com.exopet.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.ai.entity.AiDiagnosisRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AiDiagnosisService extends IService<AiDiagnosisRecord> {

    AiDiagnosisRecord diagnose(MultipartFile image, String breedType,
                               String breedName, String symptoms,
                               String symptomDesc, Long userId,
                               List<Map<String, String>> history);

    AiDiagnosisRecord moodAnalysis(MultipartFile image, String description, Long userId,
                                   List<Map<String, String>> history);

    AiDiagnosisRecord breedRecognize(MultipartFile image, String description, Long userId,
                                     List<Map<String, String>> history);
}
