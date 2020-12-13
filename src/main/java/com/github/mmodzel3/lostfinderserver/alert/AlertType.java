package com.github.mmodzel3.lostfinderserver.alert;

public enum AlertType {
    HELP(true, false),
    ANIMAL(true, false),
    FOUND_SOMETHING(true, false),
    FOUND_LOST(true, false),
    FOUND_WITNESS(true, false),
    LOST(true, false);

    private boolean showNotificationAtStart;
    private boolean showNotificationAtEnd;

    AlertType(boolean showNotificationAtStart, boolean showNotificationAtEnd) {
        this.showNotificationAtStart = showNotificationAtStart;
        this.showNotificationAtEnd = showNotificationAtEnd;
    }

    public boolean showNotificationAtStart() {
        return showNotificationAtStart;
    }

    public boolean showNotificationAtEnd() {
        return showNotificationAtEnd;
    }
}
