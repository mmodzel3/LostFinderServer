package com.github.mmodzel3.lostfinderserver.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.mmodzel3.lostfinderserver.notification.PushNotification;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationProcessingException;
import com.github.mmodzel3.lostfinderserver.notification.PushNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ChatServiceTests extends ChatTestsAbstract {
    private final int ONE_ELEMENT_SIZE = 1;
    private final int TWO_ELEMENT_SIZE = 2;
    private final int FIRST_ELEMENT = 0;
    private final int ONE_MESSAGE = 1;

    @MockBean
    private PushNotificationService pushNotificationService;

    @Autowired
    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        createTestUser();
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
    void whenSendMessageThenItIsAdded() {
        LocalDateTime now = LocalDateTime.now();
        ChatUserMessage userMessage = new ChatUserMessage(MSG, now);
        ChatMessage chatMessage = chatService.sendMessage(testUser, userMessage);
        List<ChatMessage> messages = chatRepository.findAll();

        assertEquals(TWO_ELEMENT_SIZE, messages.size());
        assertEquals(MSG, chatMessage.getMsg());
        assertEquals(testUser.getId(), chatMessage.getUser().getId());
    }

    @Test
    void whenSendMessageThenNotificationToUsersIsSendWithCorrectFormat() throws JsonProcessingException, PushNotificationProcessingException {
        ArgumentCaptor<PushNotification> argument = ArgumentCaptor.forClass(PushNotification.class);
        LocalDateTime now = LocalDateTime.now();
        ChatUserMessage userMessage = new ChatUserMessage(MSG, now);
        ChatMessage chatMessage = chatService.sendMessage(testUser, userMessage);

        verify(pushNotificationService).sendNotificationToAllUsers(argument.capture());

        assertEquals(USER_NAME, argument.getValue().getTitle());
        assertEquals(MSG, argument.getValue().getBody());
        assertEquals(ChatService.CHAT_NOTIFICATION_TYPE, argument.getValue().getType());
        assertSame(chatMessage, argument.getValue().getData());
    }

    @Test
    void whenCheckLimitAndDeleteOldMessagesAndLimitIsNotExceededThenMessagesAreNotRemoved() {
        long oldTestMessagesCount = chatRepository.count();

        chatService.checkLimitAndDeleteOldMessages();
        long newTestMessagesCount = chatRepository.count();

        assertEquals(oldTestMessagesCount, newTestMessagesCount);
    }

    @Test
    void whenCheckLimitAndDeleteOldMessagesAndLimitIsExceededThenOldMessagesAreRemoved() {
        chatService.messagesLimit = ONE_MESSAGE;
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        ChatMessage message = createTestMessage(MSG + "2", tomorrow);

        long oldTestMessagesCount = chatRepository.count();

        chatService.checkLimitAndDeleteOldMessages();
        long newTestMessagesCount = chatRepository.count();

        assertNotEquals(oldTestMessagesCount, newTestMessagesCount);
        assertEquals(tomorrow, message.getSendDate());
    }
}
