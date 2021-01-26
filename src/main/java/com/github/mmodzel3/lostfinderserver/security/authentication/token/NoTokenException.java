package com.github.mmodzel3.lostfinderserver.security.authentication.token;

import org.springframework.security.core.AuthenticationException;

public class NoTokenException extends AuthenticationException {
    public NoTokenException(String message) {
        super(message);
    }
}
