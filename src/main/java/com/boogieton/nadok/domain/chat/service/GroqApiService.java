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

    public String getAiResponse(
            List<ChatMessage> chatHistory,
            String topic,
            String bookTitle,
            String bookAuthor,
            String publicationYear
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt;

        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            systemPrompt =
                    "당신은 독서 앱 나독(NADOK)의 다정한 AI 독서 메이트 '가독이'입니다. " +
                            "현재 특정 책이 정해지지 않은 일반 독서 대화입니다. " +
                            "사용자의 독서 취향, 감정, 고민에 공감하며 따뜻한 존댓말로 답변하세요. " +
                            "답변은 반드시 한글로만 작성하세요. " +
                            "외국어, 영어 표현, 한자, 일본어, 중국어, 이모지는 사용하지 마세요. " +
                            "확실하지 않은 정보는 사실처럼 말하지 말고, 확인이 어렵다고 솔직하게 말하세요. " +
                            "책을 추천할 때는 사용자의 취향과 상황에 맞는 이유를 함께 설명하세요. " +
                            "답변은 자연스럽고 구체적으로 작성하되, 불필요하게 장황하게 늘리지 마세요.";
        } else {
            systemPrompt = String.format(
                    "당신은 독서 앱 나독(NADOK)의 다정한 AI 독서 메이트 '가독이'입니다. " +
                            "현재 사용자는 아래 책에 대해 대화하고 있습니다. " +

                            "[책 정보] " +
                            "제목: %s " +
                            "저자: %s " +
                            "출판년도: %s " +

                            "[답변 규칙] " +
                            "1. 답변은 반드시 한글로만 작성하세요. " +
                            "2. 외국어, 영어 표현, 한자, 일본어, 중국어, 이모지는 사용하지 마세요. " +
                            "3. 사용자의 질문이 책과 관련되어 있다면 반드시 위 책의 제목, 저자, 출판년도를 고려해서 답변하세요. " +
                            "4. 책의 줄거리, 인물, 주제, 문체, 상징, 메시지, 시대적 배경을 바탕으로 최대한 정확하고 자세하게 분석하세요. " +
                            "5. 확인되지 않은 내용은 단정하지 말고, '확실하게 확인하기 어렵습니다'라고 말하세요. " +
                            "6. 존재하지 않는 줄거리, 인물, 문장, 인용구를 만들어내지 마세요. " +
                            "7. 사용자가 감상이나 해석을 요청하면 근거를 들어 차분하게 설명하세요. " +
                            "8. 사용자가 스포일러를 원하지 않는 경우 핵심 결말은 말하지 마세요. " +
                            "9. 사용자의 말투와 감정에 공감하되, 답변은 존댓말로 작성하세요. " +
                            "10. 답변은 간단한 질문에는 명확하게, 분석 질문에는 충분히 자세하게 작성하세요." +
                            "답변을 작성하기 전에 사용자의 질문이 책의 줄거리, 인물, 주제, 저자, 출판년도, 감상, 해석 중 무엇을 묻는지 판단하세요. " +
                            "질문과 직접 관련 없는 내용은 줄이고, 책 정보와 대화 맥락에 근거한 내용만 답변하세요. " +
                            "정보가 부족하면 추측하지 말고, 사용자가 더 구체적인 질문을 할 수 있도록 안내하세요.",
                    bookTitle,
                    bookAuthor == null || bookAuthor.trim().isEmpty() ? "정보 없음" : bookAuthor,
                    publicationYear == null ? "정보 없음" : publicationYear
            );
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (ChatMessage msg : chatHistory) {
            String apiRole = (msg.getRole() == ChatRole.user) ? "user" : "assistant";
            messages.add(Map.of("role", apiRole, "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", messages);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GROQ_API_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            log.error("🚨 Groq API 통신 실패 구체적 원인: ", e);
            throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
        }
    }
}