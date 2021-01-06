package com.github.mmodzel3.lostfinderserver.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public
class PushNotificationService {

    static private final String NOTIFICATION_DATA_TYPE_FIELD = "type";
    static private final String NOTIFICATION_DATA_FIELD = "data";

    private final FirebaseMessaging firebaseMessaging;
    private final UserService userService;

    public PushNotificationService(FirebaseMessaging firebaseMessaging, UserService userService) {
        this.firebaseMessaging = firebaseMessaging;
        this.userService = userService;
    }

    public void sendNotificationToAllUsers(PushNotification notification) throws PushNotificationProcessingException {
        Message.Builder messageBuilder = prepareMessageFromPushNotification(notification);

        userService.getExistingUsers().forEach(user -> {
            try {
                if (user.getNotificationDestToken() != null) {
                    sendMessageToUser(user, messageBuilder);
                }
            } catch (FirebaseMessagingException e) {
                log.debug("Problem with sending notification to User: " + user.getId());
            }
        });
    }

    private Message.Builder prepareMessageFromPushNotification(PushNotification notification)
            throws PushNotificationProcessingException {
        String jsonData = parseNotificationDataToJson(notification);

        return Message.builder()
                .putData(NOTIFICATION_DATA_TYPE_FIELD, notification.getType())
                .putData(NOTIFICATION_DATA_FIELD, jsonData);
    }

    private String parseNotificationDataToJson(PushNotification notification) throws PushNotificationProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(notification.getData());
        } catch (JsonProcessingException e) {
            throw new PushNotificationProcessingException("Problem with parsing data to JSON.");
        }
    }

    private void sendMessageToUser(User user, Message.Builder messageBuilder) throws FirebaseMessagingException {
        Message message = messageBuilder.setToken(user.getNotificationDestToken()).build();
        sendMessage(message);
    }

    private void sendMessage(Message message) throws FirebaseMessagingException {
        firebaseMessaging.send(message);
    }
}
