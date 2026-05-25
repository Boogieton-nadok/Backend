package com.boogieton.nadok.domain.chat.service;

import com.boogieton.nadok.domain.chat.entity.ChatMessage;
import com.boogieton.nadok.domain.chat.entity.ChatRole;
import com.boogieton.nadok.domain.chat.exception.ChatResponseCode;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j; // 💡 1. 로깅 라이브러리 임포트 추가
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j // 💡 2. log 오브젝트를 사용할 수 있도록 롬복 어노테이션 추가
@Service
public class GroqApiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // 💡 3. 매번 new로 생성하는 대신 재사용할 수 있도록 필드로 분리 (혹은 RestTemplate 빈 주입 권장)
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAiResponse(List<ChatMessage> chatHistory, String topic, String bookTitle) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt;

        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            systemPrompt = "당신은 독서 앱 나독(NADOK)의 다정한 AI 독서 메이트 '가독이'입니다. " +
                    "현재 특정 책이 정해지지 않은 상태로 사용자와 편안하게 대화하고 있습니다. " +
                    "사용자의 독서 취향, 일상적인 고민이나 감정에 깊이 공감하며 따뜻하고 다정한 존댓말로 대화해 주세요. " +
                    "어떤 책을 읽으면 좋을지 가볍게 조언해 주어도 좋습니다. " +
                    "답변은 너무 길지 않게, 최대 200자 이내로 간결하게 작성해 주세요.";
        } else {
            systemPrompt = String.format(
                    "당신은 독서 앱 나독(NADOK)의 다정한 AI 독서 메이트 '가독이'입니다. " +
                            "현재 사용자와 책 '%s'에 대해 이야기하고 있습니다. " +
                            "반드시 이 책의 내용, 등장인물, 또는 주제를 바탕으로 대답해 주세요. " +
                            "사용자의 말에 깊이 공감하며 따뜻하고 다정한 존댓말로 대화해야 합니다. " +
                            "답변은 너무 길지 않게, 최대 200자 이내로 간결하게 작성해 주세요.", bookTitle
            );
        }

        // 1. 메시지 리스트 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        // 2. 과거 대화 내역을 순서대로 추가
        for (ChatMessage msg : chatHistory) {
            String apiRole = (msg.getRole() == ChatRole.user) ? "user" : "assistant";
            messages.add(Map.of("role", apiRole, "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.1-8b-instant");
        body.put("messages", messages);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, new HttpEntity<>(body, headers), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            // 💡 4. @Slf4j 어노테이션 덕분에 이제 컴파일 에러 없이 정상 기록됩니다.
            log.error("🚨 Groq API 통신 실패 구체적 원인: ", e);
            throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
        }
    }
}