package com.boogieton.nadok.domain.chat.controller;

import com.boogieton.nadok.domain.chat.dto.ChatDto.*;
import com.boogieton.nadok.domain.chat.service.ChatService;

import com.boogieton.nadok.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. 새로운 채팅방 생성 (201 Created)
    @PostMapping("/rooms")
    public SuccessResponse<CreateRoomRes> createRoom(@RequestBody CreateRoomReq request) {
        return SuccessResponse.created(chatService.createRoom(request));
    }

    // 2. 채팅방 목록 조회
    @GetMapping("/{userId}")
    public SuccessResponse<List<RoomListRes>> getRoomList(@PathVariable Long userId) {
        return SuccessResponse.from(chatService.getRoomList(userId));
    }

    // 채팅방 검색 조회
    @GetMapping("/{userId}/search")
    public SuccessResponse<List<RoomListRes>> searchRoomList(@PathVariable Long userId, @RequestParam String keyword){
        return SuccessResponse.from(chatService.searchRoomList(userId, keyword));
    }

    // 3. 메시지 내역 조회
    @GetMapping("/{roomId}/messages")
    public SuccessResponse<List<MessageRes>> getMessages(@PathVariable Long roomId) {
        return SuccessResponse.from(chatService.getMessages(roomId));
    }

    // 4. 메시지 전송 및 AI 응답
    @PostMapping("/{roomId}/message")
    public SuccessResponse<SendMessageRes> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendMessageReq request) {
        return SuccessResponse.from(chatService.sendMessage(roomId, request));
    }

    @DeleteMapping("/{roomId}/delete")
    public SuccessResponse<RoomListRes> deleteRoom(@PathVariable Long roomId){
        return SuccessResponse.from(chatService.deleteRoom(roomId));
    }

}