package com.boogieton.nadok.domain.mainstudy.controller;

import com.boogieton.nadok.domain.mainstudy.dto.MainStudyDto.CreateReq;
import com.boogieton.nadok.domain.mainstudy.dto.MainStudyDto.UpdateReq;
import com.boogieton.nadok.domain.mainstudy.dto.MainStudyDto.StudyListRes;
import com.boogieton.nadok.domain.mainstudy.dto.MainStudyDto.StudyDetailRes;
import com.boogieton.nadok.domain.mainstudy.exception.MainStudyResponseCode;
import com.boogieton.nadok.domain.mainstudy.service.MainStudyService;
import com.boogieton.nadok.domain.memo.dto.MemoDto.MemoRes;
import com.boogieton.nadok.domain.memo.service.MemoService;
import com.boogieton.nadok.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/main-study")
@RequiredArgsConstructor
public class MainStudyController {

    private final MainStudyService mainStudyService;
    private final MemoService memoService;

    @GetMapping
    public SuccessResponse<List<StudyListRes>> getMyStudyList(@RequestParam Long userId) {
        return SuccessResponse.from(mainStudyService.getMyStudyList(userId));
    }

    @PostMapping
    public SuccessResponse<StudyDetailRes> saveToStudy(
            @RequestParam Long userId,
            @Valid @RequestBody CreateReq req) {
        return SuccessResponse.of(mainStudyService.saveToStudy(userId, req), MainStudyResponseCode.MAIN_STUDY_SAVE_SUCCESS);
    }

    @PatchMapping("/{mainId}")
    public SuccessResponse<StudyDetailRes> updateStudy(
            @PathVariable Long mainId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateReq req) {
        return SuccessResponse.of(mainStudyService.updateStudy(userId, mainId, req), MainStudyResponseCode.MAIN_STUDY_UPDATE_SUCCESS);
    }

    @DeleteMapping("/{mainId}")
    public SuccessResponse<Void> deleteStudy(
            @PathVariable Long mainId,
            @RequestParam Long userId) {
        mainStudyService.deleteStudy(userId, mainId);
        return SuccessResponse.empty(MainStudyResponseCode.MAIN_STUDY_DELETE_SUCCESS);
    }

    @GetMapping("/{mainId}/memos")
    public SuccessResponse<List<MemoRes>> getMemosByStudy(@PathVariable Long mainId) {
        return SuccessResponse.from(memoService.getMemosByMainStudy(mainId));
    }
}
