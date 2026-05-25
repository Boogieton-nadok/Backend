package com.boogieton.nadok.domain.emotion.repository;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.boogieton.nadok.domain.emotion.entity.EmotionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmotionResultRepository extends JpaRepository<EmotionResult, Long> {

    @Query("SELECT DISTINCT r.character " +
            "FROM EmotionResult r " +
            "WHERE r.emotionInput.user.userId = :userId " +
            "AND YEAR(r.emotionInput.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(r.emotionInput.createdAt) = MONTH(CURRENT_DATE)")
    List<Character> findDistinctCharactersByUserId(@Param("userId") Long userId);
}