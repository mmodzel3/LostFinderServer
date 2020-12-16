package com.github.mmodzel3.lostfinderserver.alert;

class AlertAddPermissionException extends Exception {
    AlertAddPermissionException() {
        super("Cannot create alert. No permission to create specific type of alert.");
    }
}
