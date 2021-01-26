package com.github.mmodzel3.lostfinderserver.alert;

class AlertUpdatePermissionException extends Exception {
    AlertUpdatePermissionException() {
        super("Cannot update alert. User is not owner of alert or user is not a manager.");
    }
}
