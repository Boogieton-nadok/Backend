package com.boogieton.nadok.domain.memo.controller;

import com.boogieton.nadok.domain.memo.dto.MemoDto.*;
import com.boogieton.nadok.domain.memo.exception.MemoResponseCode;
import com.boogieton.nadok.domain.memo.service.MemoService;
import com.boogieton.nadok.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public SuccessResponse<MemoRes> createMemo(@Valid @RequestBody CreateReq req) {
        return SuccessResponse.of(memoService.createMemo(req), MemoResponseCode.MEMO_SAVE_SUCCESS);
    }

    @PatchMapping("/{memoId}")
    public SuccessResponse<MemoRes> updateMemo(
            @PathVariable Long memoId,
            @Valid @RequestBody UpdateReq req) {
        return SuccessResponse.of(memoService.updateMemo(memoId, req), MemoResponseCode.MEMO_UPDATE_SUCCESS);
    }

    @DeleteMapping("/{memoId}")
    public SuccessResponse<Void> deleteMemo(@PathVariable Long memoId) {
        memoService.deleteMemo(memoId);
        return SuccessResponse.empty(MemoResponseCode.MEMO_DELETE_SUCCESS);
    }
}
