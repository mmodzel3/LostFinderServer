package com.github.mmodzel3.lostfinderserver.alert;

class AlertDoesNotExistsException extends Exception {
    AlertDoesNotExistsException() {
        super("Alert with specific ID does not exists.");
    }
}
