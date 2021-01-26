package com.github.mmodzel3.lostfinderserver.user;

class UserUpdatePermissionException extends Exception {
    UserUpdatePermissionException() {
        super("Cannot update user. No permission to update user data.");
    }
}
