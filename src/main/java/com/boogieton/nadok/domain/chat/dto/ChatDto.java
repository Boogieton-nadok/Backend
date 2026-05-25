package com.boogieton.nadok.domain.chat.dto;

import com.boogieton.nadok.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

public class ChatDto {

    // 1. 새로운 채팅방 생성 Request & Response
    @Getter
    public static class CreateRoomReq {
        private Long userId;
        private Long bookId;
        private String topic;
    }

    @Getter
    public static class CreateRoomNoBookReq {
        private Long userId;
        private String topic;
    }

    @Getter
    @Builder
    public static class CreateRoomRes {
        private Long roomId;
        private LocalDateTime createdAt;
    }

    // 2. 채팅방 목록 조회 Response
    @Getter
    @Builder
    public static class RoomListRes {
        private Long roomId;
        private String topic;
        private Long bookId;
        private String isbn;
        private String bookTitle;
        private String bookCoverUrl;
        private LocalDateTime updatedAt;
    }

    // 3. 메시지 내역 조회 Response
    @Getter
    @Builder
    public static class MessageRes {
        private Long messageId;
        private String content;
        private String role;
        private LocalDateTime createdAt;
    }

    // 4. 메시지 전송 Request & Response
    @Getter
    public static class SendMessageReq {
        private Long userId;
        private String content;
    }

    @Getter
    @Builder
    public static class SendMessageRes {
        private MessageRes userMessage;
        private MessageRes aiMessage;
    }
}