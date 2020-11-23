package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

class ChatTestsAbstract extends AuthenticatedUserTestsAbstract {
    protected final String MSG = "Example message.";

    @Autowired
    protected ChatRepository chatRepository;

    protected ChatMessage testMessage;

    void createTestMessage() {
        LocalDateTime now = LocalDateTime.now();
        createTestUser();

        testMessage = ChatMessage.builder()
                .msg(MSG)
                .sendDate(now)
                .receivedDate(now)
                .lastUpdateDate(now)
                .user(testUser)
                .build();

        chatRepository.save(testMessage);
    }

    void deleteTestMessage() {
        chatRepository.delete(testMessage);
    }

    void deleteAllMessages() {
        chatRepository.deleteAll();
    }
}
