package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class LoginServiceTests extends UserTestsAbstract {

    private static final String FAKE_USER_EMAIL = "fake@fake.com";
    private static final String FAKE_USER_PASSWORD = "fake";

    @Autowired
    LoginService loginService;

    @BeforeEach
    void setUp() {
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenLoginToExistingUserThenGotToken() {
        String token = loginService.login(USER_EMAIL, USER_PASSWORD);

        assertNotEquals(StringUtils.EMPTY, token);
    }

    @Test
    void whenLoginToNotExistingUserThenGotNoToken() {
        String token = loginService.login(FAKE_USER_EMAIL, FAKE_USER_PASSWORD);

        assertEquals(StringUtils.EMPTY, token);
    }

    @Test
    void whenLoginWithWrongPasswordToUserThenGotNoToken() {
        String token = loginService.login(USER_EMAIL, FAKE_USER_PASSWORD);

        assertEquals(StringUtils.EMPTY, token);
    }
}
