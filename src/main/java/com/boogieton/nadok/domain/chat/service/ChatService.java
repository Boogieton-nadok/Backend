package com.boogieton.nadok.domain.chat.service;

import com.boogieton.nadok.domain.book.entity.Book;
import com.boogieton.nadok.domain.book.exception.BookResponseCode;
import com.boogieton.nadok.domain.book.repository.BookRepository;
import com.boogieton.nadok.domain.chat.dto.ChatDto.*;
import com.boogieton.nadok.domain.chat.entity.ChatMessage;
import com.boogieton.nadok.domain.chat.entity.ChatRole;
import com.boogieton.nadok.domain.chat.entity.ChatRoom;
import com.boogieton.nadok.domain.chat.exception.ChatResponseCode;
import com.boogieton.nadok.domain.chat.repository.ChatMessageRepository;
import com.boogieton.nadok.domain.chat.repository.ChatRoomRepository;
import com.boogieton.nadok.domain.user.entity.User;
import com.boogieton.nadok.domain.user.exception.UserResponseCode;
import com.boogieton.nadok.domain.user.repository.UserRepository;
import com.boogieton.nadok.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GroqApiService groqApiService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public CreateRoomRes createRoom(CreateRoomReq req) {

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new BaseException(BookResponseCode.BOOK_NOT_FOUND));

        ChatRoom room = ChatRoom.builder()
                .user(user)
                .book(book)
                .topic(req.getTopic())
                .build();
        chatRoomRepository.save(room);

        return CreateRoomRes.builder()
                .roomId(room.getRoomId())
                .createdAt(room.getCreatedAt())
                .build();
    }


    public CreateRoomRes createRoomNoBook(CreateRoomNoBookReq request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        ChatRoom room = ChatRoom.builder()
                .user(user)
                .topic(request.getTopic())
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

        if (rooms.isEmpty()) {
            throw new BaseException(ChatResponseCode.SEARCH_RESULT_NOT_FOUND);
        }
        return rooms.stream()
                .sorted(Comparator.comparing(this::getRoomActivityAt).reversed())
                .map(room -> RoomListRes.builder()
                        .roomId(room.getRoomId())
                        .topic(room.getTopic())
                        .isbn(room.getBook() == null ? null : room.getBook().getIsbn())
                        .bookId(room.getBook() == null ? null : room.getBook().getBookId())
                        .bookTitle(room.getBook() == null ? null : room.getBook().getTitle())
                        .bookCoverUrl(room.getBook() ==  null ? null : room.getBook().getCoverUrl())
                        .updatedAt(getRoomActivityAt(room))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomListRes> searchRoomList(Long userId, String keyword) {
        List<ChatRoom> rooms = chatRoomRepository.searchMyRoomsByKeyword(userId, keyword);

        if (rooms.isEmpty()) {
            throw new BaseException(ChatResponseCode.SEARCH_RESULT_NOT_FOUND);
        }

        return rooms.stream()
                .sorted(Comparator.comparing(this::getRoomActivityAt).reversed())
                .map(room -> RoomListRes.builder()
                        .roomId(room.getRoomId())
                        .topic(room.getTopic())
                        .bookId(room.getBook() == null ? null : room.getBook().getBookId())
                        .bookTitle(room.getBook() == null ? null : room.getBook().getTitle())
                        .bookCoverUrl(room.getBook() ==  null ? null : room.getBook().getCoverUrl())
                        .updatedAt(getRoomActivityAt(room))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageRes> getMessages(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findByRoomIdAndUser_UserId(roomId, userId)
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
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new BaseException(ChatResponseCode.CHAT_ROOM_NOT_FOUND));
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));

        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(room)
                .content(req.getContent())
                .role(ChatRole.user)
                .build();
        chatMessageRepository.save(userMessage);

        List<ChatMessage> fullHistory = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room);
        Book book = room.getBook();
        String title = (book != null) ? book.getTitle() : null;

        int maxMessages = 20;
        List<ChatMessage> recentHistory = fullHistory.size() > maxMessages
                ? fullHistory.subList(fullHistory.size() - maxMessages, fullHistory.size())
                : fullHistory;

        String aiResponse = groqApiService.getAiResponse(recentHistory, room.getTopic(), title);

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
    public RoomListRes deleteRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new BaseException(ChatResponseCode.CHAT_ROOM_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserResponseCode.USER_NOT_FOUND));
        RoomListRes response = RoomListRes.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .bookId(room.getBook().getBookId())
                .updatedAt(getRoomActivityAt(room))
                .build();
        chatRoomRepository.delete(room);

        return response;
    }

    private LocalDateTime getRoomActivityAt(ChatRoom room) {
        ChatMessage latestMessage = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(room);
        return latestMessage != null ? latestMessage.getCreatedAt() : room.getUpdatedAt();
    }

}
