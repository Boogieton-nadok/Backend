package com.boogieton.nadok.domain.emotion.service;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.boogieton.nadok.domain.emotion.entity.EmotionInput;
import com.boogieton.nadok.domain.emotion.entity.EmotionResult;
import com.boogieton.nadok.domain.emotion.exception.EmotionResponseCode;
import com.boogieton.nadok.domain.emotion.repository.CharacterRepository;
import com.boogieton.nadok.domain.emotion.repository.EmotionInputRepository;
import com.boogieton.nadok.domain.emotion.repository.EmotionResultRepository;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionInputReq;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionResultRes;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionTextValidateRes;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new BaseException(EmotionResponseCode.USER_NOT_FOUND));

        EmotionInput emotionInput = EmotionInput.builder()
                .inputText(request.getInputText())
                .emotionTag(request.getEmotionTag())
                .comfortMethod(request.getComfortMethod())
                .user(user)
                .build();
        emotionInputRepository.save(emotionInput);

        List<Character> characters = characterRepository.findAll();

        Long characterId = geminiApiService.analyzeEmotion(
                request.getInputText(),
                request.getEmotionTag(),
                request.getComfortMethod(),
                characters
        );

        log.info("Gemini 선택 캐릭터 ID: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new BaseException(EmotionResponseCode.CHARACTER_NOT_FOUND));

        EmotionResult emotionResult = EmotionResult.builder()
                .emotionInput(emotionInput)
                .character(character)
                .build();
        emotionResultRepository.save(emotionResult);

        return EmotionResultRes.builder()
                .inputId(emotionInput.getInputId())
                .resultId(emotionResult.getResultId())
                .character(toCharacterInfo(character))
                .createdAt(emotionInput.getCreatedAt())
                .build();
    }

    public List<String> getMonthlyEmotions(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(EmotionResponseCode.USER_NOT_FOUND));
        return emotionInputRepository.findMonthlyEmotionStats(userId);
    }

    public List<EmotionResultRes.CharacterInfo> getMetCharacters(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(EmotionResponseCode.USER_NOT_FOUND));
        return emotionResultRepository.findDistinctCharactersByUserId(userId)
                .stream()
                .map(this::toCharacterInfo)
                .collect(Collectors.toList());
    }

    private EmotionResultRes.CharacterInfo toCharacterInfo(Character character) {
        return EmotionResultRes.CharacterInfo.builder()
                .characterId(character.getCharacterId())
                .characterName(character.getCharacterName())
                .author(character.getAuthor())
                .characterImgUrl(character.getCharacterImgUrl())
                .characterThumbUrl(character.getCharacterThumbUrl())
                .bookQuote(character.getBookQuote())
                .methodReason(character.getMethodReason())
                .build();
    }
}