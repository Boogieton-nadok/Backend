package com.boogieton.nadok.domain.emotion.repository;

import com.boogieton.nadok.domain.emotion.entity.EmotionInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmotionInputRepository extends JpaRepository<EmotionInput, Long> {

    @Query("SELECT e.emotionTag " +
            "FROM EmotionInput e " +
            "WHERE e.user.userId = :userId " +
            "AND YEAR(e.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(e.createdAt) = MONTH(CURRENT_DATE) " +
            "GROUP BY e.emotionTag " +
            "ORDER BY COUNT(e) DESC " +
            "LIMIT 3")
    List<String> findMonthlyEmotionStats(@Param("userId") Long userId);
}