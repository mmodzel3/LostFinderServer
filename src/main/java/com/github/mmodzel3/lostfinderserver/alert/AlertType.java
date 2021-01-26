package com.github.mmodzel3.lostfinderserver.alert;

import com.github.mmodzel3.lostfinderserver.user.UserRole;

public enum AlertType {
    HELP(true, false, UserRole.USER),
    ANIMAL(true, false, UserRole.USER),
    FOUND_SOMETHING(true, false, UserRole.USER),
    FOUND_LOST(true, false, UserRole.USER),
    FOUND_WITNESS(true, false, UserRole.USER),
    LOST(true, false, UserRole.USER),
    SEARCH(true, true, UserRole.MANAGER),
    GATHER(true, true, UserRole.MANAGER);

    private boolean showNotificationAtStart;
    private boolean showNotificationAtEnd;
    private UserRole createUserRoleMinPermission;

    AlertType(boolean showNotificationAtStart, boolean showNotificationAtEnd, UserRole createUserRoleMinPermission) {
        this.showNotificationAtStart = showNotificationAtStart;
        this.showNotificationAtEnd = showNotificationAtEnd;
        this.createUserRoleMinPermission = createUserRoleMinPermission;
    }

    public boolean showNotificationAtStart() {
        return showNotificationAtStart;
    }

    public boolean showNotificationAtEnd() {
        return showNotificationAtEnd;
    }

    public UserRole getCreateUserRoleMinPermission() {
        return this.createUserRoleMinPermission;
    }

    public boolean checkCreatePermission(UserRole userRole) {
        if (this.createUserRoleMinPermission == UserRole.USER) {
            return true;
        } else if (this.createUserRoleMinPermission == UserRole.MANAGER) {
            return userRole == UserRole.MANAGER || userRole == UserRole.OWNER;
        } else {
            return userRole == UserRole.OWNER;
        }
    }
}
