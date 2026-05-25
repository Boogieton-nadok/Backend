package com.boogieton.nadok.domain.memo.repository;

import com.boogieton.nadok.domain.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByMainStudyMainId(Long mainId);
    List<Memo> findByMainStudyUserUserIdOrderByUpdatedAtDesc(Long userId);
}
