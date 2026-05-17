package com.boogieton.nadok.domain.emotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.boogieton.nadok.domain.emotion.entity.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
