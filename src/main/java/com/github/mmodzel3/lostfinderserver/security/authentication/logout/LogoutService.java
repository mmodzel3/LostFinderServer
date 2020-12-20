package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final UserService userService;

    LogoutService(UserService userService) {
        this.userService = userService;
    }

    public void logout(User user) {
        userService.updateUserNotificationDestToken(user, null);
    }
}
