package com.boogieton.nadok.domain.chat.service;

import com.boogieton.nadok.domain.chat.dto.ChatDto.*;
import com.boogieton.nadok.domain.chat.entity.ChatMessage;
import com.boogieton.nadok.domain.chat.entity.ChatRole;
import com.boogieton.nadok.domain.chat.entity.ChatRoom;
import com.boogieton.nadok.domain.chat.exception.ChatResponseCode;
import com.boogieton.nadok.domain.chat.repository.ChatMessageRepository;
import com.boogieton.nadok.domain.chat.repository.ChatRoomRepository;

import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroqApiService groqApiService;
    private final UserRepository userRepository;

    @Transactional
    public CreateRoomRes createRoom(CreateRoomReq req) {

        User user =  userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        ChatRoom room = ChatRoom.builder()
                .user(user)
                .bookId(req.getBookId())
                .topic(req.getTopic())
                .build();
        chatRoomRepository.save(room);

        return CreateRoomRes.builder()
                .roomId(room.getRoomId())
                .createdAt(room.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomListRes> getRoomList(Long userId) {

        List<ChatRoom> rooms = chatRoomRepository.findByUser_UserIdOrderByUpdatedAtDesc(userId);

        if(rooms.isEmpty()){
            throw new BaseException(ChatResponseCode.SEARCH_RESULT_NOT_FOUND);
        }
        return rooms.stream() // BaseEntity 필드명 updateAt 사용
                .map(room -> RoomListRes.builder()
                        .roomId(room.getRoomId())
                        .topic(room.getTopic())
                        .bookId(room.getBookId())
                        .updatedAt(room.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomListRes> searchRoomList(Long userId, String keyword) {
        List<ChatRoom> rooms = chatRoomRepository.searchMyRoomsByKeyword(userId, keyword);

        if(rooms.isEmpty()){
            throw new BaseException(ChatResponseCode.SEARCH_RESULT_NOT_FOUND);
        }

        return rooms.stream()
                .map(room -> RoomListRes.builder()
                        .roomId(room.getRoomId())
                        .topic(room.getTopic())
                        .bookId(room.getBookId())
                        .updatedAt(room.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageRes> getMessages(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ChatResponseCode.CHAT_ROOM_NOT_FOUND));

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room).stream()
                .map(msg -> MessageRes.builder()
                        .messageId(msg.getMessageId())
                        .content(msg.getContent())
                        .role(msg.getRole().name())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public SendMessageRes sendMessage(Long roomId, SendMessageReq req) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(ChatResponseCode.CHAT_ROOM_NOT_FOUND));

        // 1. 유저 메시지를 먼저 DB에 저장합니다.
        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(room)
                .content(req.getContent())
                .role(ChatRole.user)
                .build();
        chatMessageRepository.save(userMessage);

        // 2. 전체 대화 내역 조회 (문맥 유지를 위해)
        List<ChatMessage> fullHistory = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);

        // 👇 추가할 코드: '슬라이딩 윈도우' 적용 (최근 20개의 메시지만 잘라서 전송)
        int maxMessages = 20; // 가독이가 기억할 최근 메시지 개수 (원하는 대로 조절 가능)
        List<ChatMessage> recentHistory = fullHistory.size() > maxMessages
                ? fullHistory.subList(fullHistory.size() - maxMessages, fullHistory.size())
                : fullHistory;

        // 3. AI 응답 수신 (전체 내역이 아닌 최근 내역만 보냄!)
        String aiResponse = groqApiService.getAiResponse(recentHistory, room.getTopic());

        // 4. AI 메시지를 DB에 저장합니다.
        ChatMessage aiMessage = ChatMessage.builder()
                .chatRoom(room)
                .content(aiResponse)
                .role(ChatRole.ai)
                .build();
        chatMessageRepository.save(aiMessage);

        return SendMessageRes.builder()
                .userMessage(MessageRes.builder()
                        .messageId(userMessage.getMessageId())
                        .content(userMessage.getContent())
                        .role(userMessage.getRole().name())
                        .createdAt(userMessage.getCreatedAt())
                        .build())
                .aiMessage(MessageRes.builder()
                        .messageId(aiMessage.getMessageId())
                        .content(aiMessage.getContent())
                        .role(aiMessage.getRole().name())
                        .createdAt(aiMessage.getCreatedAt())
                        .build())
                .build();
    }

    @Transactional
    public RoomListRes deleteRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new BaseException(ChatResponseCode.CHAT_ROOM_NOT_FOUND));
        RoomListRes response = RoomListRes.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .bookId(room.getBookId())
                .updatedAt(room.getUpdatedAt())
                .build();
        chatRoomRepository.delete(room);

        return response;
    }
}