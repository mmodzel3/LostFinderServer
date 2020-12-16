package com.github.mmodzel3.lostfinderserver.notification;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class PushNotification {
    private String type;
    private Object data;
}