package com.boogieton.nadok.domain.emotion.service;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

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

    public GeminiAnalysisResult analyzeEmotion(String inputText, String emotionTag,
                                               String comfortMethod, List<Character> characters) {
        // 캐릭터 목록 문자열로 변환
        StringBuilder characterList = new StringBuilder();
        for (Character character : characters) {
            characterList.append(String.format(
                    "- ID: %d, 이름: %s, 명언: %s\n",
                    character.getCharacterId(),
                    character.getCharacterName(),
                    character.getBookQuote()
            ));
        }

        String prompt = String.format("""
        너는 감정 분석 전문가야. 아래 사용자의 일기와 감정 정보를 분석해서 가장 어울리는 캐릭터를 골라줘.
        
        [사용자 정보]
        일기: %s
        감정 태그: %s
        위로 방식: %s
        
        [캐릭터 목록]
        %s
        
        위 캐릭터 중 사용자에게 가장 어울리는 캐릭터를 하나 선택하고,
        이유는 위로 방식에 맞게 작성해줘.
        반드시 아래 JSON 형식으로만 응답해. 다른 말은 절대 하지마.
        {
          "characterId": 선택한 캐릭터 ID (숫자),
          "methodReason": "이 캐릭터를 선택한 이유 (2~3문장)"
        }
        """, inputText, emotionTag, comfortMethod, characterList);

        String result = callGemini(prompt);

        try {
            // JSON 파싱
            String cleaned = result.replaceAll("```json", "").replaceAll("```", "").trim();
            Map<String, Object> map = objectMapper.readValue(cleaned, Map.class);
            Long characterId = Long.valueOf(map.get("characterId").toString());
            String methodReason = (String) map.get("methodReason");
            return new GeminiAnalysisResult(characterId, methodReason);
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", result, e);
            throw new RuntimeException("AI 응답 파싱에 실패했습니다.");
        }
    }

    /**
     * Gemini API 호출 공통 메서드
     */
    private String callGemini(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

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

        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.");
        }
    }

    public record GeminiAnalysisResult(Long characterId, String methodReason) {}
}