package com.boogieton.nadok.domain.memo.service;

import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import com.boogieton.nadok.domain.mainstudy.exception.MainStudyResponseCode;
import com.boogieton.nadok.domain.mainstudy.repository.MainStudyRepository;
import com.boogieton.nadok.domain.memo.dto.MemoDto.*;
import com.boogieton.nadok.domain.memo.entity.Memo;
import com.boogieton.nadok.domain.memo.exception.MemoResponseCode;
import com.boogieton.nadok.domain.memo.repository.MemoRepository;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;
    private final MainStudyRepository mainStudyRepository;

    public List<MemoRes> getMemosByMainStudy(Long mainId) {
        return memoRepository.findByMainStudyMainId(mainId).stream()
                .map(MemoRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoRes createMemo(CreateReq req) {
        MainStudy mainStudy = mainStudyRepository.findById(req.getMainId())
                .orElseThrow(() -> new BaseException(MainStudyResponseCode.MAIN_STUDY_NOT_FOUND));

        Memo memo = Memo.builder()
                .content(req.getContent())
                .mainStudy(mainStudy)
                .build();

        return MemoRes.from(memoRepository.save(memo));
    }

    @Transactional
    public MemoRes updateMemo(Long memoId, UpdateReq req) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new BaseException(MemoResponseCode.MEMO_NOT_FOUND));

        memo.updateContent(req.getContent());
        return MemoRes.from(memo);
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new BaseException(MemoResponseCode.MEMO_NOT_FOUND));

        memoRepository.delete(memo);
    }
}
