package com.github.mmodzel3.lostfinderserver.notification;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ServerNotification {
    private String title;
    private String body;
    private Map<String, String> data;
}
