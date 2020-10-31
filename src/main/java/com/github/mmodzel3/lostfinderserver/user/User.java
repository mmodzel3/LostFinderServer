package com.github.mmodzel3.lostfinderserver.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
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
    private String email;

    @Getter(AccessLevel.NONE)
    private String password;

    private Double longitude;
    private Double latitude;

    User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
