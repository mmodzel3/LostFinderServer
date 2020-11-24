package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
class ChatService {
    final private ChatRepository chatRepository;

    @Value("${chat.messages_limit}")
    long messagesLimit;

    ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    List<ChatMessage> getMessages(int page, int pageSize) {
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("sendDate").descending());

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

    @Scheduled(cron = "${chat.clean.cron}")
    void checkLimitAndDeleteOldMessages() {
        long messagesCount = chatRepository.count();

        if (messagesCount > messagesLimit) {
            deleteOldMessages();
        }
    }

    private void deleteOldMessages() {
        List<ChatMessage> messages = chatRepository.findAll(Sort.by("sendDate").descending());
        List<ChatMessage> messagesToRemove = messages.subList((int) messagesLimit, messages.size());

        messagesToRemove.forEach(chatRepository::delete);
    }
}
