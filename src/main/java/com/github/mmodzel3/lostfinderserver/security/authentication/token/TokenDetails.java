package com.github.mmodzel3.lostfinderserver.security.authentication.token;

import lombok.Getter;

@Getter
public class TokenDetails {
    private String userEmail;

    public TokenDetails(String userEmail) {
        this.userEmail = userEmail;
    }
}
