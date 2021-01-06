package com.github.mmodzel3.lostfinderserver.security.authentication.register;

public class InvalidServerPasswordException extends Exception {
    InvalidServerPasswordException() {
        super("Invalid server password.");
    }
}
