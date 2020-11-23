package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
class ChatService {
    final private ChatRepository chatRepository;

    ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    List<ChatMessage> getMessages(int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize);

        Page<ChatMessage> messages = chatRepository.findAll(paging);
        return messages.getContent();
    }

    ChatMessage sendMessage(User user, ChatUserMessage message) {
        LocalDateTime now = LocalDateTime.now();

        ChatMessage chatMessage = ChatMessage.builder()
                .user(user)
                .msg(message.getMsg())
                .sendDate(message.getSendDate())
                .receivedDate(now)
                .lastUpdateDate(now)
                .build();

        chatRepository.save(chatMessage);

        return chatMessage;
    }
}
