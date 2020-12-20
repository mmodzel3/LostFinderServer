package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LogoutServiceTests extends AuthenticatedUserTestsAbstract {

    @Autowired
    LogoutService logoutService;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenLogoutThenUserNotificationDestTokenIsCleared() {
        logoutService.logout(testUser);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNull(possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenRemoveOldNotificationDestTokensAndNoOldNotificationsTokensThenNothingIsCleared() {
        LocalDateTime maxTokenExpirationLocalDateTime = LocalDateTime.now().minus(Duration.ofDays(1));
        logoutService.removeOldNotificationDestTokens(maxTokenExpirationLocalDateTime);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNotNull(possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenCheckLastLoginDatesAndRemoveOldNotificationDestTokensAndUserHasOldNotificationTokenThenHisTokenIsCleared() {
        LocalDateTime maxTokenExpirationLocalDateTime = LocalDateTime.now().plus(Duration.ofDays(1));
        logoutService.removeOldNotificationDestTokens(maxTokenExpirationLocalDateTime);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNull(possibleUser.get().getNotificationDestToken());
    }
}
