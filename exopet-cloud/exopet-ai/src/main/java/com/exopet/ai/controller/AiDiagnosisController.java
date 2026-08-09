package com.exopet.ai.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.exopet.ai.entity.AiDiagnosisRecord;
import com.exopet.ai.service.AiDiagnosisService;
import com.exopet.common.result.Result;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "AI诊断服务")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiDiagnosisController {

    private final AiDiagnosisService aiDiagnosisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解析 JSON 格式的对话历史
     */
    private List<Map<String, String>> parseHistory(String historyJson) {
        if (historyJson == null || historyJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(historyJson,
                    new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            log.warn("解析history失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Operation(summary = "AI问诊（对话式）")
    @PostMapping("/diagnose")
    @SentinelResource(value = "aiDiagnose", blockHandler = "diagnoseBlockHandler", fallback = "diagnoseFallback")
    public Result<AiDiagnosisRecord> diagnose(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("breedType") String breedType,
            @RequestParam("breedName") String breedName,
            @RequestParam("symptoms") String symptoms,
            @RequestParam(value = "symptomDesc", required = false) String symptomDesc,
            @RequestParam(value = "history", required = false) String historyJson,
            @RequestHeader("userId") Long userId) {

        List<Map<String, String>> history = parseHistory(historyJson);
        AiDiagnosisRecord result = aiDiagnosisService.diagnose(image, breedType, breedName, symptoms, symptomDesc, userId, history);
        return Result.success(result);
    }

    public Result<AiDiagnosisRecord> diagnoseBlockHandler(
            MultipartFile image, String breedType, String breedName,
            String symptoms, String symptomDesc, String historyJson,
            Long userId, BlockException e) {
        return Result.failed("AI诊断当前繁忙，请稍后重试");
    }

    public Result<AiDiagnosisRecord> diagnoseFallback(
            MultipartFile image, String breedType, String breedName,
            String symptoms, String symptomDesc, String historyJson,
            Long userId, Throwable e) {
        log.error("AI诊断异常", e);
        return Result.failed("AI服务暂时不可用: " + e.getMessage());
    }

    @Operation(summary = "情绪分析（对话式，支持图文）")
    @PostMapping("/mood-analysis")
    @SentinelResource(value = "aiMoodAnalysis", blockHandler = "moodAnalysisBlockHandler", fallback = "moodAnalysisFallback")
    public Result<AiDiagnosisRecord> moodAnalysis(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam(value = "history", required = false) String historyJson,
            @RequestHeader("userId") Long userId) {

        List<Map<String, String>> history = parseHistory(historyJson);
        AiDiagnosisRecord result = aiDiagnosisService.moodAnalysis(image, description, userId, history);
        return Result.success(result);
    }

    public Result<AiDiagnosisRecord> moodAnalysisBlockHandler(
            MultipartFile image, String description, String historyJson, Long userId, BlockException e) {
        return Result.failed("情绪分析当前繁忙");
    }

    public Result<AiDiagnosisRecord> moodAnalysisFallback(
            MultipartFile image, String description, String historyJson, Long userId, Throwable e) {
        return Result.failed("情绪分析暂时不可用");
    }

    @Operation(summary = "拍照识宠（对话式，支持图文）")
    @PostMapping("/breed-recognize")
    @SentinelResource(value = "aiBreedRecognize", blockHandler = "breedRecognizeBlockHandler", fallback = "breedRecognizeFallback")
    public Result<AiDiagnosisRecord> breedRecognize(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam(value = "history", required = false) String historyJson,
            @RequestHeader("userId") Long userId) {

        List<Map<String, String>> history = parseHistory(historyJson);
        AiDiagnosisRecord result = aiDiagnosisService.breedRecognize(image, description, userId, history);
        return Result.success(result);
    }

    public Result<AiDiagnosisRecord> breedRecognizeBlockHandler(
            MultipartFile image, String description, String historyJson, Long userId, BlockException e) {
        return Result.failed("识宠当前繁忙");
    }

    public Result<AiDiagnosisRecord> breedRecognizeFallback(
            MultipartFile image, String description, String historyJson, Long userId, Throwable e) {
        return Result.failed("识宠暂时不可用");
    }
}
