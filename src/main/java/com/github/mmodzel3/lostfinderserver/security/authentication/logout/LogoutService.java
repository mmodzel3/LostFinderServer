package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogoutService {

    private final UserService userService;

    @Value("${jwt.expiration:86400000}")
    private Long tokenExpiration;

    LogoutService(UserService userService) {
        this.userService = userService;
    }

    public void logout(User user) {
        userService.clearLoggedUserData(user);
    }

    @Scheduled(cron = "${logout.notification.clean.cron}")
    void checkLastLoginDatesAndRemoveOldNotificationDestTokens() {
        LocalDateTime maxTokenExpirationLocalDateTime = LocalDateTime.now().minus(Duration.ofMillis(tokenExpiration));

        removeOldNotificationDestTokens(maxTokenExpirationLocalDateTime);
    }

    void removeOldNotificationDestTokens(LocalDateTime maxTokenExpirationLocalDateTime) {
        List<User> users = userService.getAllUsers();

        users.stream()
                .filter(u -> u.getLastLoginDate() != null && u.getLastLoginDate().isBefore(maxTokenExpirationLocalDateTime))
                .forEach(this::logout);
    }
}
