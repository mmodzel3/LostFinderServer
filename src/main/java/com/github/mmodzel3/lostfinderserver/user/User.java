package com.github.mmodzel3.lostfinderserver.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "user")
@Getter
@Setter
public class User {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    private String username;

    @Setter(AccessLevel.NONE)
    @Indexed(unique = true)
    private String email;

    private String password;

    private Double longitude;
    private Double latitude;

    @Indexed(unique = true)
    private String token;

    User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
