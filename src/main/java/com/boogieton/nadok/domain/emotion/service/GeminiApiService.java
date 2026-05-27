package com.boogieton.nadok.domain.emotion.service;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.boogieton.nadok.domain.emotion.exception.EmotionResponseCode;
import com.boogieton.nadok.global.exception.BaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean validateDiary(String inputText) {
        String prompt = String.format("""
                아래 텍스트가 사람의 감정이나 하루 경험을 담은 일기 형식의 글인지 판단해줘.
                일기라면 true, 아니라면 false만 반환해. 다른 말은 절대 하지마.
                
                텍스트: %s
                """, inputText);

        String result = callGemini(prompt);
        return result.trim().equalsIgnoreCase("true");
    }

    public Long analyzeEmotion(String inputText, String emotionTag,
                               String comfortMethod, List<Character> characters) {
        StringBuilder characterList = new StringBuilder();
        for (Character character : characters) {
            characterList.append(String.format(
                    "- ID: %d, 이름: %s, 키워드: %s, 명언: %s\n",
                    character.getCharacterId(),
                    character.getCharacterName(),
                    character.getKeyword(),
                    character.getBookQuote()
            ));
        }

        String prompt = String.format("""
                너는 감정 분석 전문가야. 아래 사용자의 일기와 감정 정보를 분석해서 가장 어울리는 캐릭터를 골라줘.
                캐릭터의 키워드가 사용자의 감정 태그와 잘 맞는지 참고해줘.
                
                [사용자 정보]
                일기: %s
                감정 태그: %s
                위로 방식: %s
                
                [캐릭터 목록]
                %s
                
                위 캐릭터 중 사용자에게 가장 어울리는 캐릭터를 하나 선택하고,
                반드시 아래 JSON 형식으로만 응답해. 다른 말은 절대 하지마.
                {
                  "characterId": 선택한 캐릭터 ID (숫자)
                }
                """, inputText, emotionTag, comfortMethod, characterList);



        String result = callGemini(prompt);

        System.out.println(result);

        try {
            String cleaned = result.replaceAll("```json", "").replaceAll("```", "").trim();
            Map<String, Object> map = objectMapper.readValue(cleaned, Map.class);
            return Long.valueOf(map.get("characterId").toString());
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", result, e);
            throw new BaseException(EmotionResponseCode.AI_RESPONSE_PARSE_ERROR);
        }
    }

    private String callGemini(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        int maxAttempts = 3;
        long delayMs = 2000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(
                        GEMINI_API_URL + apiKey,
                        new HttpEntity<>(body, headers),
                        Map.class
                );

                List<Map<String, Object>> candidates =
                        (List<Map<String, Object>>) response.getBody().get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");

            } catch (HttpStatusCodeException e) {
                log.warn("Gemini API 호출 실패 (시도 {}/{}): 상태 코드 {}", attempt, maxAttempts, e.getStatusCode());

                if (attempt == maxAttempts) {
                    if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        throw new BaseException(EmotionResponseCode.AI_REQUEST_CONFUSION_ERROR);
                    }
                    throw new BaseException(EmotionResponseCode.AI_SERVICE_ERROR);
                }

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BaseException(EmotionResponseCode.AI_SERVICE_ERROR);
                }

            } catch (Exception e) {
                log.error("Gemini API 알 수 없는 오류: {}", e.getMessage());
                throw new BaseException(EmotionResponseCode.AI_SERVICE_ERROR);
            }
        }

        throw new BaseException(EmotionResponseCode.AI_SERVICE_ERROR);
    }
}