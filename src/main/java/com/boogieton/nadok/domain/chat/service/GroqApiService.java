package com.boogieton.nadok.domain.chat.service;

import com.boogieton.nadok.domain.chat.entity.ChatMessage;
import com.boogieton.nadok.domain.chat.entity.ChatRole;
import com.boogieton.nadok.domain.chat.exception.ChatResponseCode;
import com.boogieton.nadok.global.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqApiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // 단일 메시지가 아닌, 채팅 내역 전체(chatHistory)를 받도록 변경
    public String getAiResponse(List<ChatMessage> chatHistory, String topic, String bookTitle) {
        RestTemplate restTemplate = new RestTemplate();
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
        }
        else {
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
            // Groq API는 AI의 역할을 'assistant'로 인식하므로 변환해줍니다.
            String apiRole = (msg.getRole() == ChatRole.user) ? "user" : "assistant";
            messages.add(Map.of("role", apiRole, "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.1-8b-instant");
        body.put("messages", messages); // 구성한 메시지 리스트를 바디에 담음

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, new HttpEntity<>(body, headers), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            System.out.println("🚨 Groq API 에러 원인: " + e.getMessage());
            throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
        }
    }
}