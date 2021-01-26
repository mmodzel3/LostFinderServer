package com.github.mmodzel3.lostfinderserver.notification;

public class PushNotificationProcessingException extends Exception {

    PushNotificationProcessingException(String message) {
        super("Problem with push notification processing: " + message);
    }
}
