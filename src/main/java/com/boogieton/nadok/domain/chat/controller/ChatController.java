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

    @PostMapping("/rooms")
    public SuccessResponse<CreateRoomRes> createRoom(@RequestBody CreateRoomReq request) {
        return SuccessResponse.created(chatService.createRoom(request));
    }

    @GetMapping("/{userId}")
    public SuccessResponse<List<RoomListRes>> getRoomList(@PathVariable Long userId) {
        return SuccessResponse.from(chatService.getRoomList(userId));
    }

    @GetMapping("/{userId}/search")
    public SuccessResponse<List<RoomListRes>> searchRoomList(@PathVariable Long userId, @RequestParam String keyword) {
        return SuccessResponse.from(chatService.searchRoomList(userId, keyword));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public SuccessResponse<List<MessageRes>> getMessages(@RequestParam Long userId, @PathVariable Long roomId) {
        return SuccessResponse.from(chatService.getMessages(userId, roomId));
    }

    @PostMapping("/rooms/{roomId}/message")
    public SuccessResponse<SendMessageRes> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendMessageReq request) {
        return SuccessResponse.from(chatService.sendMessage(roomId, request));
    }

    @DeleteMapping("/rooms/{roomId}/delete")
    public SuccessResponse<RoomListRes> deleteRoom(@RequestParam Long userId, @PathVariable Long roomId) {
        return SuccessResponse.from(chatService.deleteRoom(userId, roomId));
    }
}
