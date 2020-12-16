package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.notification.PushNotification;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationService;
import com.github.mmodzel3.lostfinderserver.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
class ChatService {
    static final String CHAT_NOTIFICATION_TYPE = "chat";

    final private ChatRepository chatRepository;
    final private PushNotificationService pushNotificationService;

    @Value("${chat.messages_limit}")
    long messagesLimit;

    ChatService(ChatRepository chatRepository, PushNotificationService pushNotificationService) {
        this.chatRepository = chatRepository;
        this.pushNotificationService = pushNotificationService;
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

        try {
            sendChatMessageNotificationToUsers(chatMessage);
        } catch (PushNotificationProcessingException e) {
            log.debug("Problem with sending chat message as notification to all users. " + e.getMessage());
        }

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

    private void sendChatMessageNotificationToUsers(ChatMessage message) throws PushNotificationProcessingException {
        PushNotification notification = PushNotification.builder()
                .type(CHAT_NOTIFICATION_TYPE)
                .data(message)
                .build();

        pushNotificationService.sendNotificationToAllUsers(notification);
    }
}
