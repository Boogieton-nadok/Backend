package com.boogieton.nadok.domain.emotion.repository;

import com.boogieton.nadok.domain.emotion.entity.EmotionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionResultRepository extends JpaRepository<EmotionResult, Long> {
}
