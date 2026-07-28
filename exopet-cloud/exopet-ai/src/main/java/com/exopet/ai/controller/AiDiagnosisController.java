package com.exopet.ai.controller;

import com.exopet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Tag(name = "AI诊断服务")
@RestController
@RequestMapping("/api/ai")
public class AiDiagnosisController {

    private final ChatClient chatClient;

    public AiDiagnosisController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Operation(summary = "AI问诊")
    @PostMapping("/diagnose")
    public Result<DiagnosisResult> diagnose(
            @RequestParam("image") MultipartFile image,
            @RequestParam("breedType") String breedType,
            @RequestParam("symptoms") String symptoms,
            @RequestParam(value = "description", required = false) String description) throws IOException {

        String prompt = """
            你是一个资深的异宠兽医。请根据以下信息进行诊断。
            【宠物类型】%s
            【症状标签】%s
            【用户描述】%s

            请以JSON格式返回：
            {
                "possibleDiseases": [{"name":"疾病名","probability":0.8,"severity":"低/中/高"}],
                "carePlan": {"temperature":"温度建议","diet":"饮食建议"},
                "medicationWarnings": ["用药禁忌提示"]
            }
            """.formatted(breedType, symptoms, description != null ? description : "");

        DiagnosisResult result = new DiagnosisResult();

        try {
            String response = chatClient.prompt()
                    .messages(new UserMessage(prompt,
                            List.of(new Media(MimeTypeUtils.IMAGE_PNG,
                                    new ByteArrayResource(image.getBytes())))))
                    .call()
                    .content();
            result.setRawResponse(response);
        } catch (Exception e) {
            log.error("AI诊断调用失败", e);
            return Result.failed("AI服务暂时不可用，请稍后重试");
        }

        return Result.success(result);
    }

    @Operation(summary = "情绪分析")
    @PostMapping("/mood-analysis")
    public Result<MoodResult> moodAnalysis(@RequestParam("image") MultipartFile image) throws IOException {

        String prompt = """
            你是一个异宠行为学专家。分析这张照片中宠物的情绪状态。
            以JSON返回：{"mood":"开心/紧张/不适","confidence":0.85,"advice":"建议"}
            """;

        MoodResult result = new MoodResult();

        try {
            String response = chatClient.prompt()
                    .messages(new UserMessage(prompt,
                            List.of(new Media(MimeTypeUtils.IMAGE_PNG,
                                    new ByteArrayResource(image.getBytes())))))
                    .call()
                    .content();
            result.setRawResponse(response);
        } catch (Exception e) {
            log.error("情绪分析调用失败", e);
            return Result.failed("AI服务暂时不可用，请稍后重试");
        }

        return Result.success(result);
    }

    @Operation(summary = "拍照识宠")
    @PostMapping("/breed-recognize")
    public Result<BreedResult> breedRecognize(@RequestParam("image") MultipartFile image) throws IOException {

        String prompt = """
            识别这张照片中的异宠品种。以JSON返回：
            {"breedName":"品种名","sciName":"学名","description":"品种说明","characteristics":["特征1","特征2"]}
            """;

        BreedResult result = new BreedResult();

        try {
            String response = chatClient.prompt()
                    .messages(new UserMessage(prompt,
                            List.of(new Media(MimeTypeUtils.IMAGE_PNG,
                                    new ByteArrayResource(image.getBytes())))))
                    .call()
                    .content();
            result.setRawResponse(response);
        } catch (Exception e) {
            log.error("拍照识宠调用失败", e);
            return Result.failed("AI服务暂时不可用，请稍后重试");
        }

        return Result.success(result);
    }

    // ---- 内部DTO ----

    @Data
    public static class DiagnosisResult {
        private String rawResponse;
    }

    @Data
    public static class MoodResult {
        private String rawResponse;
    }

    @Data
    public static class BreedResult {
        private String rawResponse;
    }
}
