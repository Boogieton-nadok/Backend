package com.boogieton.nadok.domain.memo.service;

import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import com.boogieton.nadok.domain.mainstudy.exception.MainStudyResponseCode;
import com.boogieton.nadok.domain.mainstudy.repository.MainStudyRepository;
import com.boogieton.nadok.domain.memo.dto.MemoDto.*;
import com.boogieton.nadok.domain.memo.entity.Memo;
import com.boogieton.nadok.domain.memo.exception.MemoResponseCode;
import com.boogieton.nadok.domain.memo.repository.MemoRepository;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.exception.UserResponseCode;
import com.boogieton.nadok.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public List<MemoRes> getMemosByMainStudy(Long mainId) {
        return memoRepository.findByMainStudyMainId(mainId).stream()
                .map(MemoRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoDetailRes createMemo(CreateReq req) {
        MainStudy mainStudy = mainStudyRepository.findById(req.getMainId())
                .orElseThrow(() -> new BaseException(MainStudyResponseCode.MAIN_STUDY_NOT_FOUND));

        Memo memo = Memo.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .mainStudy(mainStudy)
                .build();

        memoRepository.save(memo);

        return MemoDetailRes.builder()
                .memoId(memo.getMemoId())
                .title(memo.getTitle())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .build();
    }

    @Transactional
    public MemoDetailRes updateMemo(Long memoId, UpdateReq req) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new BaseException(MemoResponseCode.MEMO_NOT_FOUND));

        memo.updateTitle(req.getTitle());
        memo.updateContent(req.getContent());
        return MemoDetailRes.builder()
                .memoId(memo.getMemoId())
                .title(memo.getTitle())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new BaseException(MemoResponseCode.MEMO_NOT_FOUND));

        memoRepository.delete(memo);
    }

    @Transactional(readOnly = true)
    public List<MemoRes> getMemoList(Long userId){
        // 1. 유저가 존재하는지 먼저 검증 (기존 예외 처리 활용)
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        // 2. 해당 유저가 소유한 모든 책의 메모 리스트를 한 번에 가져옴
        List<Memo> memoList = memoRepository.findByMainStudyUserUserIdOrderByUpdatedAtDesc(userId);

        // 3. 엔티티 리스트를 MemoRes DTO 리스트로 변환하여 반환
        return memoList.stream()
                .map(MemoRes::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemoDetailRes getMemoDetail(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new BaseException(MemoResponseCode.MEMO_NOT_FOUND));
        return MemoDetailRes.builder()
                .memoId(memoId)
                .title(memo.getTitle())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .build();
    }

}
