package com.github.mmodzel3.lostfinderserver.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class LoginServiceTests extends UserTestsAbstract {

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
        deleteTestUser();

        assertNotEquals(StringUtils.EMPTY, token);
    }
}
