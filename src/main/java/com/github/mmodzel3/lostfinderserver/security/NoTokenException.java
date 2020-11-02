package com.github.mmodzel3.lostfinderserver.security;

import org.springframework.security.core.AuthenticationException;

class NoTokenException extends AuthenticationException {
    NoTokenException(String message) {
        super(message);
    }
}
