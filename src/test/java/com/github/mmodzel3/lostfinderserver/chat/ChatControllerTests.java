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
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTests extends ChatTestsAbstract {
    private final int ONE_ELEMENT_SIZE = 1;
    private final int TWO_ELEMENT_SIZE = 2;
    private final int FIRST_ELEMENT = 0;

    @LocalServerPort
    int port;

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
        ChatMessage[] messages = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .param("page", 0)
                .param("pageSize", 10)
                .get("/api/chat")
                .then()
                .statusCode(200)
                .extract()
                .as(ChatMessage[].class);

        assertEquals(ONE_ELEMENT_SIZE, messages.length);
        assertEquals(MSG, messages[FIRST_ELEMENT].getMsg());
        assertEquals(testUser.getId(), messages[FIRST_ELEMENT].getUser().getId());
    }

    @Test
    void whenSendMessageThenItIsSend() {
        LocalDateTime now = LocalDateTime.now();
        ChatUserMessage userMessage = new ChatUserMessage(MSG, now);

        ChatMessage message = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userMessage)
                .post("/api/chat")
                .then()
                .statusCode(200)
                .extract()
                .as(ChatMessage.class);

        List<ChatMessage> messages = chatRepository.findAll();

        assertEquals(TWO_ELEMENT_SIZE, messages.size());
        assertEquals(MSG, message.getMsg());
        assertEquals(testUser.getId(), message.getUser().getId());
    }

    @Test
    void whenSendMessageThenNotificationIsSendToAllUsers() throws JsonProcessingException, PushNotificationProcessingException {
        ArgumentCaptor<PushNotification> argument = ArgumentCaptor.forClass(PushNotification.class);
        LocalDateTime now = LocalDateTime.now();
        ChatUserMessage userMessage = new ChatUserMessage(MSG, now);

        ChatMessage message = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(userMessage)
                .post("/api/chat")
                .then()
                .statusCode(200)
                .extract()
                .as(ChatMessage.class);

        verify(pushNotificationService).sendNotificationToAllUsers(argument.capture());

        ChatMessage notificationChatMessage = (ChatMessage) argument.getValue().getData();

        assertEquals(ChatService.CHAT_NOTIFICATION_TYPE, argument.getValue().getType());
        assertEquals(MSG, notificationChatMessage.getMsg());
    }
}
