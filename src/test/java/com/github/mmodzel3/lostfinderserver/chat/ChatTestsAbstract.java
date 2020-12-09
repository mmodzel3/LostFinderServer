package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.sun.jna.platform.win32.WinUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

class ChatTestsAbstract extends AuthenticatedUserTestsAbstract {
    protected final String MSG = "Example message.";

    @Autowired
    protected ChatRepository chatRepository;

    protected ChatMessage testMessage;

    ChatMessage createTestMessage(String message, LocalDateTime sendDateTime) {
        testMessage = ChatMessage.builder()
                .msg(MSG)
                .sendDate(sendDateTime)
                .receivedDate(sendDateTime)
                .lastUpdateDate(sendDateTime)
                .user(testUser)
                .build();

        chatRepository.save(testMessage);

        return testMessage;
    }

    ChatMessage createTestMessage() {
        LocalDateTime now = LocalDateTime.now();
        return createTestMessage(MSG, now);
    }

    void deleteTestMessage() {
        chatRepository.delete(testMessage);
    }

    void deleteAllMessages() {
        chatRepository.deleteAll();
    }
}
