package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests extends AuthenticatedUserTestsAbstract {
    private final int ONE_ELEMENT_LIST_SIZE = 1;

    private final double TEST_LATITUDE = 20.2;
    private final double TEST_LONGITUDE = 23.2;

    private final String TEST_NOTIFICATION_DEST_TOKEN = "token";

    private final String USER_NEW_PASSWORD = "new_password";
    private final String USER_INVALID_PASSWORD = "bad_password";

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        deleteAllUsers();
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenGetAllUsersThenGotAll() {
        User[] users = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .get("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User[].class);

        assertEquals(ONE_ELEMENT_LIST_SIZE, users.length);
        assertEquals(USER_EMAIL, users[0].getEmail());
        assertEquals(USER_ROLE, users[0].getRole());
    }

    @Test
    void whenUpdateUserLocationThenGotItUpdated() {
        Location location = new Location(TEST_LATITUDE, TEST_LONGITUDE);

        ServerResponse response = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .body(location)
                .post("/api/user/location")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertEquals(TEST_LATITUDE, possibleUser.get().getLocation().getLatitude());
        assertEquals(TEST_LONGITUDE, possibleUser.get().getLocation().getLongitude());
        assertNotEquals(testUser.getLastUpdateDate(), possibleUser.get().getLastUpdateDate());
    }

    @Test
    void whenUpdateNotificationDestTokenThenGotItUpdated() {
        ServerResponse response = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("token", TEST_NOTIFICATION_DEST_TOKEN)
                .post("/api/user/notification/token")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertEquals(TEST_NOTIFICATION_DEST_TOKEN, possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenUpdateUserPasswordWithBadPasswordThenItIsNotUpdated() {
        ServerResponse response = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("oldPassword", USER_INVALID_PASSWORD)
                .param("newPassword", USER_NEW_PASSWORD)
                .post("/api/user/password")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER_EMAIL);

        assertEquals(ServerResponse.INVALID_PARAM, response);
        assertTrue(possibleUser.isPresent());
        assertTrue(passwordEncoder.matches(USER_PASSWORD, possibleUser.get().getPassword()));
    }

    @Test
    void whenUpdateUserPasswordWithCorrectPasswordThenItIsUpdated() {
        ServerResponse response = given().port(port)
                .header(AUTHROIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("oldPassword", USER_PASSWORD)
                .param("newPassword", USER_NEW_PASSWORD)
                .post("/api/user/password")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertTrue(passwordEncoder.matches(USER_NEW_PASSWORD, possibleUser.get().getPassword()));
    }
}
