package com.github.mmodzel3.lostfinderserver.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ChatUserMessage {
    private String msg;
    private LocalDateTime sendDate;
}
