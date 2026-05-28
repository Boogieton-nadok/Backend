package com.boogieton.nadok.domain.chat.service;

import com.boogieton.nadok.domain.chat.entity.ChatMessage;
import com.boogieton.nadok.domain.chat.entity.ChatRole;
import com.boogieton.nadok.domain.chat.exception.ChatResponseCode;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroqApiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final int DEFAULT_MAX_RESPONSE_LENGTH = 350;
    private static final int DETAIL_MAX_RESPONSE_LENGTH = 650;

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

        String systemPrompt = buildSystemPrompt(topic, bookTitle, bookAuthor, publicationYear);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (ChatMessage msg : chatHistory) {
            String apiRole = (msg.getRole() == ChatRole.user) ? "user" : "assistant";
            messages.add(Map.of("role", apiRole, "content", msg.getContent()));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", messages);

        // 응답 길이와 창의성 조절
        body.put("max_tokens", 450);
        body.put("temperature", 0.3);
        body.put("top_p", 0.8);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GROQ_API_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            if (response.getBody() == null || response.getBody().get("choices") == null) {
                throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");

            if (choices.isEmpty() || choices.get(0).get("message") == null) {
                throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            return sanitizeAiResponse(content);

        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Groq API 통신 실패 구체적 원인: ", e);
            throw new BaseException(ChatResponseCode.AI_RESPONSE_ERROR);
        }
    }

    private String buildSystemPrompt(
            String topic,
            String bookTitle,
            String bookAuthor,
            String publicationYear
    ) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            return buildGeneralPrompt(topic);
        }

        return buildBookPrompt(topic, bookTitle, bookAuthor, publicationYear);
    }

    private String buildGeneralPrompt(String topic) {
        return String.format(
                "당신은 독서 앱 나독의 다정한 인공지능 독서 메이트 가독이입니다. " +

                        "현재 대화 주제는 다음과 같습니다. " +
                        "대화 주제: %s " +

                        "답변 규칙은 반드시 지켜야 합니다. " +
                        "첫째, 모든 답변은 반드시 존댓말로만 작성하세요. " +
                        "둘째, 사용자가 반말로 말해도 절대 반말로 답하지 마세요. " +
                        "셋째, 반말, 명령조, 비꼬는 말투, 친구처럼 가벼운 말투를 사용하지 마세요. " +
                        "넷째, 답변은 반드시 한글 문장 중심으로 작성하세요. " +
                        "다섯째, 한자, 중국어 문자, 일본어 문자는 절대 사용하지 마세요. " +
                        "여섯째, 영어 단어와 영어 약자는 되도록 사용하지 말고 한글 표현으로 바꾸세요. " +
                        "일곱째, 이모지와 과한 특수기호를 사용하지 마세요. " +
                        "여덟째, 확실하지 않은 정보는 단정하지 말고 확인하기 어렵다고 말하세요. " +
                        "아홉째, 사용자의 감정에 공감하되 과장하지 말고 차분하게 답하세요. " +
                        "열째, 답변은 기본적으로 300자 이내로 작성하세요. " +
                        "열한째, 꼭 필요한 경우에도 500자를 넘기지 마세요. " +
                        "열두째, 문장은 짧고 명확하게 작성하세요. " +

                        "좋은 말투 예시는 다음과 같습니다. " +
                        "좋습니다. 그렇게 느끼실 수 있습니다. 이 책은 이런 점에서 도움이 될 수 있습니다. " +
                        "피해야 할 말투는 다음과 같습니다. " +
                        "좋아. 그랬어. 읽어봐. 맞아. 그렇지.",
                safeValue(topic)
        );
    }

    private String buildBookPrompt(
            String topic,
            String bookTitle,
            String bookAuthor,
            String publicationYear
    ) {
        return String.format(
                "당신은 독서 앱 나독의 다정한 인공지능 독서 메이트 가독이입니다. " +

                        "현재 사용자는 아래 책에 대해 대화하고 있습니다. " +
                        "대화 주제: %s " +
                        "책 제목: %s " +
                        "저자: %s " +
                        "출판년도: %s " +

                        "답변 규칙은 반드시 지켜야 합니다. " +
                        "첫째, 모든 답변은 반드시 존댓말로만 작성하세요. " +
                        "둘째, 사용자가 반말로 말해도 절대 반말로 답하지 마세요. " +
                        "셋째, 반말, 명령조, 비꼬는 말투, 친구처럼 가벼운 말투를 사용하지 마세요. " +
                        "넷째, 답변은 반드시 한글 문장 중심으로 작성하세요. " +
                        "다섯째, 한자, 중국어 문자, 일본어 문자는 절대 사용하지 마세요. " +
                        "여섯째, 영어 단어와 영어 약자는 되도록 사용하지 말고 한글 표현으로 바꾸세요. " +
                        "일곱째, 이모지와 과한 특수기호를 사용하지 마세요. " +

                        "책 관련 답변 규칙은 다음과 같습니다. " +
                        "첫째, 사용자의 질문이 책과 관련되어 있다면 반드시 위 책의 제목, 저자, 출판년도를 고려하세요. " +
                        "둘째, 책의 줄거리, 인물, 주제, 문체, 상징, 메시지, 시대적 배경을 바탕으로 답변하세요. " +
                        "셋째, 존재하지 않는 줄거리, 인물, 문장, 인용구를 만들어내지 마세요. " +
                        "넷째, 정확하지 않은 내용은 단정하지 말고 확인하기 어렵다고 말하세요. " +
                        "다섯째, 책 제목이나 저자명에 한자, 중국어 문자, 일본어 문자가 포함되어 있어도 답변에는 그대로 쓰지 마세요. " +
                        "여섯째, 사용자가 해석을 요청하면 근거를 들어 차분하게 설명하세요. " +
                        "일곱째, 사용자가 스포일러를 원하지 않는 경우 결말이나 핵심 반전을 말하지 마세요. " +

                        "응답 길이 규칙은 다음과 같습니다. " +
                        "간단한 질문은 250자 이내로 답하세요. " +
                        "일반적인 책 대화는 350자 이내로 답하세요. " +
                        "사용자가 자세한 분석, 깊은 해석, 비교 분석을 요청한 경우에도 600자를 넘기지 마세요. " +
                        "불필요한 인사말, 반복 설명, 장황한 배경 설명은 줄이세요. " +

                        "좋은 말투 예시는 다음과 같습니다. " +
                        "그렇게 느끼신 점이 충분히 이해됩니다. 이 장면은 인물의 변화가 드러나는 부분으로 볼 수 있습니다. " +
                        "피해야 할 말투는 다음과 같습니다. " +
                        "맞아. 좋아. 읽어봐. 그랬어. 그렇지.",
                safeValue(topic),
                safeValue(bookTitle),
                safeValue(bookAuthor),
                safeValue(publicationYear)
        );
    }

    private String sanitizeAiResponse(String text) {
        if (text == null) {
            return "";
        }

        String result = text;

        // 한자와 중국어 문자 제거
        result = result.replaceAll("[\\u3400-\\u4DBF\\u4E00-\\u9FFF\\uF900-\\uFAFF]", "");

        // 일본어 히라가나, 가타카나 제거
        result = result.replaceAll("[\\u3040-\\u309F\\u30A0-\\u30FF]", "");

        // 이모지와 일부 특수 기호 제거
        result = result.replaceAll("[\\p{So}\\p{Cn}]", "");

        // 공백 정리
        result = result.replaceAll("\\s+", " ").trim();

        // 응답이 너무 길 경우 한 번 더 자르기
        result = limitResponseLength(result);

        // 혹시 반말 느낌으로 끝나는 경우 부드럽게 보정
        result = polishHonorificEnding(result);

        return result;
    }

    private String limitResponseLength(String text) {
        if (text == null) {
            return "";
        }

        int maxLength = text.contains("분석")
                || text.contains("해석")
                || text.contains("비교")
                || text.contains("상징")
                || text.contains("주제")
                ? DETAIL_MAX_RESPONSE_LENGTH
                : DEFAULT_MAX_RESPONSE_LENGTH;

        if (text.length() <= maxLength) {
            return text;
        }

        String shortened = text.substring(0, maxLength);

        int lastSentenceEnd = Math.max(
                shortened.lastIndexOf("."),
                Math.max(shortened.lastIndexOf("!"), shortened.lastIndexOf("?"))
        );

        if (lastSentenceEnd > 100) {
            shortened = shortened.substring(0, lastSentenceEnd + 1);
        }

        return shortened.trim();
    }

    private String polishHonorificEnding(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String result = text.trim();

        // 너무 기계적으로 모든 문장을 바꾸지는 않고, 마지막이 명백히 반말처럼 끝날 때만 보정합니다.
        if (result.endsWith("해")
                || result.endsWith("했어")
                || result.endsWith("좋아")
                || result.endsWith("맞아")
                || result.endsWith("그렇지")
                || result.endsWith("읽어봐")) {
            result += "요";
        }

        return result;
    }

    private String safeValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "정보 없음";
        }

        return value.trim();
    }
}
