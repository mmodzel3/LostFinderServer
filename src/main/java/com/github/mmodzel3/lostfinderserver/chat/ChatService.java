package com.github.mmodzel3.lostfinderserver.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mmodzel3.lostfinderserver.notification.NotificationService;
import com.github.mmodzel3.lostfinderserver.notification.ServerNotification;
import com.github.mmodzel3.lostfinderserver.user.User;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
class ChatService {
    final private ChatRepository chatRepository;
    final private NotificationService notificationService;

    @Value("${chat.messages_limit}")
    long messagesLimit;

    ChatService(ChatRepository chatRepository, NotificationService notificationService) {
        this.chatRepository = chatRepository;
        this.notificationService = notificationService;
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

        sendChatMessageNotificationToUsers(chatMessage);

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

    private void sendChatMessageNotificationToUsers(ChatMessage message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            json = StringUtil.EMPTY_STRING;
        }

        Map<String, String> data = new HashMap<String, String>();
        data.put("message", json);

        ServerNotification notification = ServerNotification.builder()
                .title(message.getUser().getUsername())
                .body(message.getMsg())
                .data(data)
                .build();

        notificationService.sendNotificationToAllUsers(notification);
    }
}
