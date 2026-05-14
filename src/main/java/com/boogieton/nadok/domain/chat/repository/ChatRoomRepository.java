package com.boogieton.nadok.domain.chat.repository;

import com.boogieton.nadok.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUser_UserIdOrderByUpdatedAtDesc(Long userId);
    @Query("SELECT c FROM ChatRoom c WHERE c.user.userId = :userId AND c.topic LIKE %:keyword% ORDER BY c.updatedAt DESC")
    List<ChatRoom> searchMyRoomsByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}