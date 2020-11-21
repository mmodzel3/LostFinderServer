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
}
