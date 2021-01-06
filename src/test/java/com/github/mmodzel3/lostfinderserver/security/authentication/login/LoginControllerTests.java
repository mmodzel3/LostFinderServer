package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTests extends UserTestsAbstract {

    private static final String FAKE_USER_EMAIL = "fake@fake.com";
    private static final String FAKE_USER_PASSWORD = "fake";
    private static final String USER_NOTIFICATION_DEST_TOKEN = "notification_token";

    @Autowired
    UserService userService;

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
    void whenLoginToExistingUserThenGotTokenAndUserRole() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertNotEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertEquals(testUser.getRole(), loginInfo.getRole());
    }

    @Test
    void whenLoginToExistingUserThenUserLastLoginDateIsUpdated() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNotNull(possibleUser.get().getLastLoginDate());
    }

    @Test
    void whenLoginToNonExistingUserThenGotNoTokenAndRoleNotLogged() {
        LoginInfo loginInfo = given().port(port)
                .param("email", FAKE_USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertEquals(UserRole.NOT_LOGGED, loginInfo.getRole());
    }

    @Test
    void whenLoginWithWrongPasswordThenGotLoginInfoWithNoTokenAndRoleNotLogged() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertEquals(UserRole.NOT_LOGGED, loginInfo.getRole());
    }

    @Test
    void whenLoginToExistingUserWithNotificationDestTokenThenUserIsUpdated() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("pushNotificationDestToken", USER_NOTIFICATION_DEST_TOKEN)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertNotEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertTrue(possibleUser.isPresent());
        assertEquals(USER_NOTIFICATION_DEST_TOKEN, possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenLoginWithWrongPasswordAndNotificationDestTokenThenUserIsNotUpdated() {
        LoginInfo loginInfo = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", FAKE_USER_PASSWORD)
                .param("pushNotificationDestToken", USER_NOTIFICATION_DEST_TOKEN)
                .post("login")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginInfo.class);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertEquals(StringUtils.EMPTY, loginInfo.getToken());
        assertTrue(possibleUser.isPresent());
        assertEquals(testUser.getNotificationDestToken(), possibleUser.get().getNotificationDestToken());
    }
}
