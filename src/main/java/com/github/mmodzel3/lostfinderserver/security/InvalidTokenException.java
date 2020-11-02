package com.github.mmodzel3.lostfinderserver.security;

import org.springframework.security.core.AuthenticationException;

class InvalidTokenException extends AuthenticationException {
    InvalidTokenException(String message) {
        super(message);
    }
}
