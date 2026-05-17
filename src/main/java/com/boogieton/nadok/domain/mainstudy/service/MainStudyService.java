package com.boogieton.nadok.domain.mainstudy.service;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.repository.BookRepository;
import com.boogieton.nadok.domain.mainstudy.dto.MainStudyDto.*;
import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import com.boogieton.nadok.domain.mainstudy.exception.MainStudyResponseCode;
import com.boogieton.nadok.domain.mainstudy.repository.MainStudyRepository;
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
public class MainStudyService {

    private final MainStudyRepository mainStudyRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public List<StudyListRes> getMyStudyList(Long userId) {
        return mainStudyRepository.findByUserUserId(userId).stream()
                .map(StudyListRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyDetailRes saveToStudy(Long userId, CreateReq req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        Book book = bookRepository.findByIsbn(req.getIsbn())
                .orElseGet(() -> bookRepository.save(
                        Book.builder()
                                .title(req.getTitle())
                                .author(req.getAuthor())
                                .isbn(req.getIsbn())
                                .coverUrl(req.getCoverUrl())
                                .bookIntro(req.getBookIntro())
                                .publisher(req.getPublisher())
                                .pageCount(req.getPageCount())
                                .build()
                ));

        if (mainStudyRepository.existsByUserUserIdAndBookBookId(userId, book.getBookId())) {
            throw new BaseException(MainStudyResponseCode.MAIN_STUDY_ALREADY_EXISTS);
        }

        MainStudy mainStudy = MainStudy.builder()
                .user(user)
                .book(book)
                .readingStatus(req.getReadingStatus())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();

        return StudyDetailRes.from(mainStudyRepository.save(mainStudy));
    }

    @Transactional
    public StudyDetailRes updateStudy(Long userId, Long mainId, UpdateReq req) {
        MainStudy mainStudy = mainStudyRepository.findById(mainId)
                .orElseThrow(() -> new BaseException(MainStudyResponseCode.MAIN_STUDY_NOT_FOUND));

        if (!mainStudy.getUser().getUserId().equals(userId)) {
            throw new BaseException(MainStudyResponseCode.MAIN_STUDY_ACCESS_DENIED);
        }

        mainStudy.update(req.getReadingStatus(), req.getStartDate(), req.getEndDate());
        return StudyDetailRes.from(mainStudy);
    }

    @Transactional
    public void deleteStudy(Long userId, Long mainId) {
        MainStudy mainStudy = mainStudyRepository.findById(mainId)
                .orElseThrow(() -> new BaseException(MainStudyResponseCode.MAIN_STUDY_NOT_FOUND));

        if (!mainStudy.getUser().getUserId().equals(userId)) {
            throw new BaseException(MainStudyResponseCode.MAIN_STUDY_ACCESS_DENIED);
        }

        mainStudyRepository.delete(mainStudy);
    }
}
