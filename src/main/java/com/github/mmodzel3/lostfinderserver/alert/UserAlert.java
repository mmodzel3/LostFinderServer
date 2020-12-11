package com.github.mmodzel3.lostfinderserver.alert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.user.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserAlert {
    private String type;

    private Location location;

    private Double range;

    private String title;

    private String description;

    @Getter(AccessLevel.NONE)
    private Boolean showNotificationAtStart;

    @Getter(AccessLevel.NONE)
    private Boolean showNotificationAtEnd;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendDate;

    public Boolean showNotificationAtStart() {
        return showNotificationAtStart;
    }

    public Boolean showNotificationAtEnd() {
        return showNotificationAtEnd;
    }
}
