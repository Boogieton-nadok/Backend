package com.boogieton.nadok.domain.emotion.web.controller;

import com.boogieton.nadok.domain.emotion.service.EmotionService;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionResultRes;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionInputReq;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionTextValidateReq;
import com.boogieton.nadok.domain.emotion.web.dto.EmotionTextValidateRes;
import com.boogieton.nadok.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion-inputs")
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/input-text")
    public ResponseEntity<SuccessResponse<EmotionTextValidateRes>> validateText(
            @Valid @RequestBody EmotionTextValidateReq request
    ) {
        EmotionTextValidateRes response = emotionService.validateInputText(
                request.getInputText()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.from(response));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<EmotionResultRes>> analyzeEmotion(
            @Valid @RequestBody EmotionInputReq request
    ) {
        EmotionResultRes response = emotionService.analyzeEmotion(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created(response));
    }
}