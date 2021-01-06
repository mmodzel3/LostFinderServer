package com.github.mmodzel3.lostfinderserver.notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushNotification {
    private String type;
    private Object data;
}
