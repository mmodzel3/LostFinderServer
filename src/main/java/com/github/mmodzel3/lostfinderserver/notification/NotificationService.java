package com.github.mmodzel3.lostfinderserver.notification;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.google.firebase.messaging.*;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class NotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserService userService;

    public NotificationService(FirebaseMessaging firebaseMessaging, UserService userService) {
        this.firebaseMessaging = firebaseMessaging;
        this.userService = userService;
    }

    public String sendNotificationToUser(User user, ServerNotification notification) throws FirebaseMessagingException {
        Message message = convertNotificationToMessage(notification, user.getNotificationDestToken());
        return firebaseMessaging.send(message);
    }

    public void sendNotificationToAllUsers(ServerNotification notification) {
        userService.getAllUsers().forEach(user -> {
            try {
                if (user.getNotificationDestToken() != null) {
                    sendNotificationToUser(user, notification);
                }
            } catch (FirebaseMessagingException e) {
                log.debug("Problem with sending notification to User: " + user.getId());
            }
        });
    }

    private Message convertNotificationToMessage(ServerNotification notification, String notificationDestToken) {
        Notification messagingNotification = Notification.builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getBody())
                .build();

        return Message.builder()
                .putAllData(notification.getData())
                .setNotification(messagingNotification)
                .setToken(notificationDestToken)
                .build();
    }
}
