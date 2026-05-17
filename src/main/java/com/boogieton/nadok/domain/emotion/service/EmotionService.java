package com.boogieton.nadok.domain.emotion.service;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.boogieton.nadok.domain.emotion.entity.EmotionInput;
import com.boogieton.nadok.domain.emotion.entity.EmotionResult;
import com.boogieton.nadok.domain.emotion.repository.CharacterRepository;
import com.boogieton.nadok.domain.emotion.repository.EmotionInputRepository;
import com.boogieton.nadok.domain.emotion.repository.EmotionResultRepository;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionInputReq;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionResultRes;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionTextValidateRes;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {

    private final EmotionInputRepository emotionInputRepository;
    private final EmotionResultRepository emotionResultRepository;
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final GeminiApiService geminiApiService;

    public EmotionTextValidateRes validateInputText(String inputText) {
        boolean isValid = geminiApiService.validateDiary(inputText);
        return EmotionTextValidateRes.builder()
                .isValid(isValid)
                .build();
    }

    @Transactional
    public EmotionResultRes analyzeEmotion(EmotionInputReq request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        EmotionInput emotionInput = EmotionInput.builder()
                .inputText(request.getInputText())
                .emotionTag(request.getEmotionTag())
                .comfortMethod(request.getComfortMethod())
                .user(user)
                .build();
        emotionInputRepository.save(emotionInput);

        List<Character> characters = characterRepository.findAll();

        GeminiApiService.GeminiAnalysisResult aiResult = geminiApiService.analyzeEmotion(
                request.getInputText(),
                request.getEmotionTag(),
                request.getComfortMethod(),
                characters
        );

        log.info("Gemini 선택 캐릭터 ID: {}", aiResult.characterId());

        Character character = characterRepository.findById(aiResult.characterId())
                .orElseThrow(() -> new IllegalArgumentException("캐릭터를 찾을 수 없습니다."));

        // 6. EmotionResult 저장
        EmotionResult emotionResult = EmotionResult.builder()
                .emotionInput(emotionInput)
                .character(character)
                .methodReason(aiResult.methodReason())
                .build();
        emotionResultRepository.save(emotionResult);

        // 7. Response 반환
        return EmotionResultRes.builder()
                .inputId(emotionInput.getInputId())
                .resultId(emotionResult.getResultId())
                .methodReason(emotionResult.getMethodReason())
                .character(EmotionResultRes.CharacterInfo.builder()
                        .characterId(character.getCharacterId())
                        .characterName(character.getCharacterName())
                        .characterImgUrl(character.getCharacterImgUrl())
                        .bookQuote(character.getBookQuote())
                        .build())
                .createdAt(emotionInput.getCreatedAt())
                .build();
    }
}