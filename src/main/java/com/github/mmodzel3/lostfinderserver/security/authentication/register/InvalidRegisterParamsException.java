package com.github.mmodzel3.lostfinderserver.security.authentication.register;

public class InvalidRegisterParamsException extends Exception {
    InvalidRegisterParamsException() {
        super("Invalid register params data.");
    }
}
