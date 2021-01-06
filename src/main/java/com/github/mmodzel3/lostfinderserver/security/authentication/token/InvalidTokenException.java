package com.github.mmodzel3.lostfinderserver.security.authentication.token;

public class InvalidTokenException extends Exception {
    InvalidTokenException(String message) {
        super(message);
    }
}
