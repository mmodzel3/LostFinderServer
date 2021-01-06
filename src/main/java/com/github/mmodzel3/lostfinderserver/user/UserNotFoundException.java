package com.github.mmodzel3.lostfinderserver.user;

class UserNotFoundException extends Exception {
    UserNotFoundException() {
        super("User does not exists.");
    }
}
