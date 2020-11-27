package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginServiceTests extends UserTestsAbstract {

    private static final String FAKE_USER_EMAIL = "fake@fake.com";
    private static final String FAKE_USER_PASSWORD = "fake";
    private static final String USER_NOTIFICATION_DEST_TOKEN = "notification_token";

    @Autowired
    LoginService loginService;

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
    void whenLoginToExistingUserThenGotLoginInfoWithToken() {
        LoginInfo loginInfo = loginService.login(USER_EMAIL, USER_PASSWORD);

        assertNotEquals(StringUtils.EMPTY, loginInfo.getToken());
    }

    @Test
    void whenLoginToNotExistingUserThenGotLoginInfoWithNoToken() {
        LoginInfo loginInfo = loginService.login(FAKE_USER_EMAIL, FAKE_USER_PASSWORD);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
    }

    @Test
    void whenLoginWithWrongPasswordToUserThenGotLoginInfoWithNoToken() {
        LoginInfo loginInfo = loginService.login(USER_EMAIL, FAKE_USER_PASSWORD);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
    }

    @Test
    void whenLoginWithNotificationDestTokenToExistingUserThenUserIsUpdated() {
        LoginInfo loginInfo = loginService.login(USER_EMAIL, USER_PASSWORD, USER_NOTIFICATION_DEST_TOKEN);
        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);

        assertNotEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertTrue(possibleUser.isPresent());
        assertEquals(USER_NOTIFICATION_DEST_TOKEN, possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenLoginWithWrongPasswordAndNotificationDestTokenToUserThenUserIsNotUpdated() {
        LoginInfo loginInfo = loginService.login(USER_EMAIL, FAKE_USER_PASSWORD, USER_NOTIFICATION_DEST_TOKEN);
        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertTrue(possibleUser.isPresent());
        assertEquals(testUser.getNotificationDestToken(), possibleUser.get().getNotificationDestToken());
    }
}
