package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.user.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ChatMessage {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @DBRef
    private User user;

    private String msg;

    private LocalDateTime receivedDate;
    private LocalDateTime sendDate;
    private LocalDateTime lastUpdateDate;
}
