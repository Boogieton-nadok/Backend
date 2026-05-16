package com.boogieton.nadok.domain.mainstudy.repository;

import com.boogieton.nadok.domain.mainstudy.entity.MainStudy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MainStudyRepository extends JpaRepository<MainStudy, Long> {
    List<MainStudy> findByUserUserId(Long userId);
    Optional<MainStudy> findByUserUserIdAndBookIsbn(Long userId, String isbn);
    boolean existsByUserUserIdAndBookBookId(Long userId, Long bookId);
}
