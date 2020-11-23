package com.github.mmodzel3.lostfinderserver.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ChatServiceTests extends ChatTestsAbstract {
    private final int ONE_ELEMENT_SIZE = 1;
    private final int TWO_ELEMENT_SIZE = 2;
    private final int FIRST_ELEMENT = 0;

    @Autowired
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        createTestMessage();
    }

    @AfterEach
    void tearDown() {
        deleteAllMessages();
        deleteAllUsers();
    }

    @Test
    void whenGetMessagesThenGotThem() {
        List<ChatMessage> messages = chatService.getMessages(0, 1);

        assertEquals(ONE_ELEMENT_SIZE, messages.size());
        assertEquals(MSG, messages.get(FIRST_ELEMENT).getMsg());
        assertEquals(testUser.getId(), messages.get(FIRST_ELEMENT).getUser().getId());
    }

    @Test
    void whenSendMessageThenItIsSend() {
        LocalDateTime now = LocalDateTime.now();
        ChatUserMessage userMessage = new ChatUserMessage(MSG, now);
        ChatMessage chatMessage = chatService.sendMessage(testUser, userMessage);
        List<ChatMessage> messages = chatRepository.findAll();

        assertEquals(TWO_ELEMENT_SIZE, messages.size());
        assertEquals(MSG, chatMessage.getMsg());
        assertEquals(testUser.getId(), chatMessage.getUser().getId());
    }
}
