package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.user.AuthenticatedUserTestsAbstract;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LogoutControllerTests extends AuthenticatedUserTestsAbstract {

    @LocalServerPort
    int port;

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
    void whenLogoutThenUserNotificationDestTokenAndLocationIsCleared() {
        given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .post("/api/logout")
                .then()
                .statusCode(200);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNull(possibleUser.get().getNotificationDestToken());
        assertNull(possibleUser.get().getLocation());
        assertTrue(testUser.getLastUpdateDate().isBefore(possibleUser.get().getLastUpdateDate()));
    }
}
