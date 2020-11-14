package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTests extends UserTestsAbstract {

    private static final String FAKE_USER_EMAIL = "fake@fake.com";
    private static final String FAKE_USER_PASSWORD = "fake";

    @LocalServerPort
    int port;

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
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertNotEquals(StringUtils.EMPTY, loginInfo.getToken());
    }

    @Test
    void whenLoginToNonExistingUserThenGotNoToken() {
        LoginInfo loginInfo = given().port(port)
                .param("email", FAKE_USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
    }

    @Test
    void whenLoginWithWrongPasswordThenGotLoginInfoWithNoToken() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
    }
}
