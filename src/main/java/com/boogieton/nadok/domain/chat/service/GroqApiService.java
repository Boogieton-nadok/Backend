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
    public String getAiResponse(List<ChatMessage> chatHistory, String topic) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = String.format("너는 '나독(NADOK)'의 AI '가독이'야. 주제: %s. 다정하게 존댓말로 대답해줘.", topic);

        // 1. 메시지 리스트 구성
        List<Map<String, String>> messages = new ArrayList<>();
        // 시스템 프롬프트 추가
        messages.add(Map.of("role", "system", "content", systemPrompt));

        // 2. 과거 대화 내역을 순서대로 추가
        for (ChatMessage msg : chatHistory) {
            // Groq API는 AI의 역할을 'assistant'로 인식하므로 변환해줍니다.
            String apiRole = (msg.getRole() == ChatRole.user) ? "user" : "assistant";
            messages.add(Map.of("role", apiRole, "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "Llama 3.3 70B Versatile");
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