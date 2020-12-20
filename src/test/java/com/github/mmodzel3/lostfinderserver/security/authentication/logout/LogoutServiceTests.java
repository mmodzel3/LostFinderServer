package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
