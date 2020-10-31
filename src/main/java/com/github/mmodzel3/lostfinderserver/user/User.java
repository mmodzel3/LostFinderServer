package com.github.mmodzel3.lostfinderserver.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
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

    private UserRole role;

    public User() {

    }

    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }
}
