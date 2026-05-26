package com.boogieton.nadok.domain.memo.controller;

import com.boogieton.nadok.domain.memo.dto.MemoDto;
import com.boogieton.nadok.domain.memo.dto.MemoDto.*;
import com.boogieton.nadok.domain.memo.exception.MemoResponseCode;
import com.boogieton.nadok.domain.memo.service.MemoService;
import com.boogieton.nadok.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping("/{userId}")
    public SuccessResponse<List<MemoRes>> getMemoList(@PathVariable Long userId){
        // 💡 memoService.getMemoList(userId) 로 파라미터를 정확히 넘겨주도록 수정했습니다.
        return SuccessResponse.of(memoService.getMemoList(userId), MemoResponseCode.MEMO_LIST_GET_SUCCESS);
    }

    @GetMapping("/{userId}/{memoId}")
    public SuccessResponse<MemoDetailRes>  getMemoDetail(@PathVariable Long userId, @PathVariable Long memoId){
        return SuccessResponse.of(memoService.getMemoDetail(memoId), MemoResponseCode.MEMO_GET_SUCCESS);
    }

    @PostMapping
    public SuccessResponse<MemoDetailRes> createMemo(@Valid @RequestBody CreateReq req) {
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
