package com.exopet.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.ai.entity.AiDiagnosisRecord;
import com.exopet.ai.mapper.AiDiagnosisRecordMapper;
import com.exopet.ai.service.AiDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiDiagnosisServiceImpl extends ServiceImpl<AiDiagnosisRecordMapper, AiDiagnosisRecord>
        implements AiDiagnosisService {

    private final ChatClient chatClient;

    public AiDiagnosisServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public AiDiagnosisRecord diagnose(MultipartFile image, String breedType,
                                      String breedName, String symptoms,
                                      String symptomDesc, Long userId,
                                      List<Map<String, String>> history) {
        long start = System.currentTimeMillis();

        String prompt = buildConversationPrompt("""
            你是一个资深的异宠兽医助手。你的任务是通过对话逐步了解宠物病情，最终给出诊断。

            ## 行为规则
            1. **第一轮**：根据用户描述的症状，分析可能性，然后追问1-2个关键问题（如排泄情况、精神状态、饮食变化等）。
            2. **中间轮**：根据用户回答深入分析，继续追问缺失的关键信息。
            3. **最终轮**：当信息足够时，给出完整诊断结论和养护方案。

            ## 回答格式（必须返回合法JSON，不要markdown代码块）
            第一轮/中间轮：
            {
              "reply": "你的回复内容",
              "nextQuestion": "你追问的问题",
              "isDone": false
            }
            最终轮：
            {
              "reply": "总结分析",
              "nextQuestion": "",
              "isDone": true,
              "conclusion": {
                "possibleDiseases": [{"name":"疾病名","probability":0.8,"severity":"低/中/高"}],
                "carePlan": {"temperature":"温度建议","diet":"饮食建议","medication":"用药建议"},
                "confidence": 0.85
              }
            }
            """,
                "宠物信息",
                "宠物类型：%s，具体品种：%s，症状标签：%s，用户描述：%s".formatted(
                        breedType != null ? breedType : "",
                        breedName != null ? breedName : "",
                        symptoms != null ? symptoms : "",
                        symptomDesc != null ? symptomDesc : ""),
                history);

        return callAi(image, prompt, "diagnose", userId, start);
    }

    @Override
    public AiDiagnosisRecord moodAnalysis(MultipartFile image, String description, Long userId,
                                          List<Map<String, String>> history) {
        long start = System.currentTimeMillis();

        String prompt = buildConversationPrompt("""
            你是一个异宠行为学专家。你的任务是通过对话分析宠物的情绪状态。

            ## 行为规则
            1. **第一轮**：根据用户描述的宠物行为，分析可能的情绪状态，追问细节。
            2. **后续轮次**：根据补充信息深入分析。
            3. **最终轮**：给出完整的情绪分析结论。

            ## 回答格式（必须返回合法JSON，不要markdown代码块）
            中间轮：
            {"reply":"回复内容","nextQuestion":"追问的问题","isDone":false}
            最终轮：
            {"reply":"总结","nextQuestion":"","isDone":true,"conclusion":{"mood":"情绪","confidence":0.85,"advice":"建议"}}
            """,
                "宠物行为描述",
                description != null ? description : "用户还没有提供描述，请引导用户描述宠物行为",
                history);

        return callAi(image, prompt, "mood", userId, start);
    }

    @Override
    public AiDiagnosisRecord breedRecognize(MultipartFile image, String description, Long userId,
                                            List<Map<String, String>> history) {
        long start = System.currentTimeMillis();

        String prompt = buildConversationPrompt("""
            你是一个异宠品种识别专家。你的任务是通过对话确定宠物的品种。

            ## 行为规则
            1. **第一轮**：根据用户描述的外形特征，猜测可能的品种，追问更多特征。
            2. **后续轮次**：根据补充特征缩小范围。
            3. **最终轮**：给出确定的品种信息和说明。

            ## 回答格式（必须返回合法JSON，不要markdown代码块）
            中间轮：
            {"reply":"回复内容","nextQuestion":"追问的问题","isDone":false}
            最终轮：
            {"reply":"总结","nextQuestion":"","isDone":true,"conclusion":{"breedName":"品种","sciName":"学名","description":"说明","characteristics":["特征"]}}
            """,
                "宠物特征描述",
                description != null ? description : "用户还没有提供描述，请引导用户描述宠物的外形特征",
                history);

        return callAi(image, prompt, "breed", userId, start);
    }

    /**
     * 构建带对话历史的 Prompt
     */
    private String buildConversationPrompt(String systemPrompt, String infoTitle,
                                            String userInfo, List<Map<String, String>> history) {
        StringBuilder sb = new StringBuilder();
        sb.append(systemPrompt).append("\n\n");
        sb.append("## ").append(infoTitle).append("\n").append(userInfo).append("\n\n");

        if (history != null && !history.isEmpty()) {
            sb.append("## 对话历史\n");
            for (Map<String, String> turn : history) {
                if (turn.containsKey("user")) {
                    sb.append("用户：").append(turn.get("user")).append("\n");
                }
                if (turn.containsKey("ai")) {
                    sb.append("你：").append(turn.get("ai")).append("\n");
                }
            }
            sb.append("\n");
        }

        sb.append("用户的最新回复如上。请根据对话历史判断这是第几轮，并按规则回复。\n");
        sb.append("如果信息足够，请给出最终结论（isDone: true）；如果还需要更多信息，请追问（isDone: false）。");
        return sb.toString();
    }

    /**
     * 统一 AI 调用 + 落库
     */
    private AiDiagnosisRecord callAi(MultipartFile image, String prompt, String type,
                                      Long userId, long startMs) {

        AiDiagnosisRecord record = new AiDiagnosisRecord();
        record.setUserId(userId);
        if (image != null && !image.isEmpty()) {
            record.setImages("[\"" + image.getOriginalFilename() + "\"]");
        }

        try {
            String response;
            if (image != null && !image.isEmpty()) {
                MimeType mimeType = MimeTypeUtils.parseMimeType(
                        image.getContentType() != null ? image.getContentType() : "image/png");
                response = chatClient.prompt()
                        .messages(new UserMessage(prompt,
                                List.of(new Media(mimeType,
                                        new ByteArrayResource(image.getBytes())))))
                        .call()
                        .content();
            } else {
                response = chatClient.prompt()
                        .messages(new UserMessage(prompt))
                        .call()
                        .content();
            }

            String cleaned = cleanJsonResponse(response);
            record.setAiRawResponse(cleaned);
            record.setDiseaseList(cleaned);
            record.setConfidence(BigDecimal.valueOf(0.80));
            record.setAiModel("Qwen3-VL-8B-Instruct");

        } catch (Exception e) {
            log.error("AI调用失败 type={}", type, e);
            String errMsg = e.getMessage() != null ?
                    e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") :
                    "unknown error";
            record.setAiRawResponse("{\"error\":\"" + errMsg + "\"}");
            record.setConfidence(BigDecimal.ZERO);
        }

        record.setDurationMs((int) (System.currentTimeMillis() - startMs));
        record.setCreatedAt(LocalDateTime.now());
        save(record);
        return record;
    }

    /**
     * 清洗 AI 返回内容，去掉 markdown 代码块标记
     */
    private String cleanJsonResponse(String raw) {
        if (raw == null) return "{}";
        String cleaned = raw.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```(?:json)?\\s*", "");
            cleaned = cleaned.replaceAll("\\s*```$", "");
            cleaned = cleaned.trim();
        }

        try {
            new com.fasterxml.jackson.databind.ObjectMapper().readTree(cleaned);
            return cleaned;
        } catch (Exception e) {
            return "{\"raw\":\"" + cleaned.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
        }
    }
}
