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
        String token = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertNotEquals(StringUtils.EMPTY, token);
    }

    @Test
    void whenLoginToNonExistingUserThenGotNoToken() {
        String token = given().port(port)
                .param("email", FAKE_USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertEquals(StringUtils.EMPTY, token);
    }

    @Test
    void whenLoginWithWrongPasswordThenGotNoToken() {
        String token = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertEquals(StringUtils.EMPTY, token);
    }
}
