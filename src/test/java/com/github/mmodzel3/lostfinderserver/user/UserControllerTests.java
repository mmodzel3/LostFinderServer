package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
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
class UserControllerTests extends AuthenticatedUserTestsAbstract {
    private static final int ONE_ELEMENT_LIST_SIZE = 1;

    private static final double TEST_LATITUDE = 20.2;
    private static final double TEST_LONGITUDE = 23.2;

    private static final String TEST_NOTIFICATION_DEST_TOKEN = "token";

    private static final String USER_NEW_PASSWORD = "new_password";
    private static final String USER_INVALID_PASSWORD = "bad_password";

    private static final String USER2_EMAIL = "user2@example.com";
    private static final String USER2_NAME = "user2";

    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final String USER_TOO_SHORT_PASSWORD = "123";

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        userService.minPasswordLength = MIN_PASSWORD_LENGTH;

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
                .header(AUTHORIZATION, authorizationHeader)
                .param("all", true)
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
    void whenGetExistingUsersThenGotNotDeletedUsers() {
        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        user.setDeleted(true);
        userRepository.save(user);

        User[] users = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .get("/api/users")
                .then()
                .statusCode(200)
                .extract()
                .as(User[].class);

        assertEquals(ONE_ELEMENT_LIST_SIZE, users.length);
    }

    @Test
    void whenUpdateUserLocationThenGotItUpdated() {
        Location location = new Location(TEST_LATITUDE, TEST_LONGITUDE);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
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
    void whenUpdateUserLocationWithNullLatitudeThenLocationIsCleared() {
        Location location = new Location(null, TEST_LONGITUDE);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
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
        assertNull(possibleUser.get().getLocation());
        assertNotEquals(testUser.getLastUpdateDate(), possibleUser.get().getLastUpdateDate());
    }

    @Test
    void whenUpdateUserLocationWithNullLongitudeThenLocationIsCleared() {
        Location location = new Location(TEST_LATITUDE, null);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
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
        assertNull(possibleUser.get().getLocation());
        assertNotEquals(testUser.getLastUpdateDate(), possibleUser.get().getLastUpdateDate());
    }

    @Test
    void whenClearUserLocationThenItIsCleared() {
        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .post("/api/user/location/clear")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertNull(possibleUser.get().getLocation());
        assertNotEquals(testUser.getLastUpdateDate(), possibleUser.get().getLastUpdateDate());
    }

    @Test
    void whenUpdateNotificationDestTokenThenGotItUpdated() {
        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
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
                .header(AUTHORIZATION, authorizationHeader)
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
                .header(AUTHORIZATION, authorizationHeader)
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

    @Test
    void whenUpdateUserPasswordWithTooShortPasswordThenItIsNotUpdated() {
        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("oldPassword", USER_PASSWORD)
                .param("newPassword", USER_TOO_SHORT_PASSWORD)
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
    void whenUpdateUserRoleForUserThatDoesNotExistThenNotFoundResponseIsReturned() {
        changeTestUserRole(UserRole.OWNER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("role", UserRole.MANAGER)
                .post("/api/user/role")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.NOT_FOUND, response);
    }

    @Test
    void whenUpdateUserRoleUsingUserPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.USER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("role", UserRole.MANAGER)
                .post("/api/user/role")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenUpdateUserRoleUsingManagerPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("role", UserRole.MANAGER)
                .post("/api/user/role")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenUpdateUserRoleUsingOwnerPermissionThenUserRoleIsUpdated() {
        changeTestUserRole(UserRole.OWNER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("role", UserRole.MANAGER)
                .post("/api/user/role")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertEquals(UserRole.MANAGER, possibleUser.get().getRole());
    }

    @Test
    void whenUpdateUserBlockForUserThatDoesNotExistThenNotFoundResponseIsReturned() {
        changeTestUserRole(UserRole.OWNER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.NOT_FOUND, response);
    }

    @Test
    void whenUpdateUserBlockUsingUserPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.USER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenUpdateUserBlockForManagerUsingManagerPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.MANAGER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenUpdateUserBlockForOwnerUsingManagerPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.OWNER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenUpdateUserRoleForUserUsingManagerPermissionThenUserRoleIsUpdated() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertTrue(possibleUser.get().isBlocked());
    }

    @Test
    void whenUpdateUserBlockUsingOwnerPermissionThenUserRoleIsUpdated() {
        changeTestUserRole(UserRole.OWNER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .param("isBlocked", true)
                .post("/api/user/block")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertTrue(possibleUser.isPresent());
        assertTrue(possibleUser.get().isBlocked());
    }

    @Test
    void whenDeleteUserForUserThatDoesNotExistThenNotFoundResponseIsReturned() {
        changeTestUserRole(UserRole.OWNER);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.NOT_FOUND, response);
    }

    @Test
    void whenDeleteUserUsingUserPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.USER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenDeleteUserForManagerUsingManagerPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.MANAGER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenDeleteUserForOwnerUsingManagerPermissionThenInvalidPermissionStatusIsReturned() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.OWNER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }

    @Test
    void whenDeleteUserForUserUsingManagerPermissionThenUserIsDeleted() {
        changeTestUserRole(UserRole.MANAGER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertFalse(possibleUser.isPresent());
    }

    @Test
    void whenDeleteUserUsingOwnerPermissionThenUserIsDeleted() {
        changeTestUserRole(UserRole.OWNER);

        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, UserRole.USER);
        userRepository.save(user);

        ServerResponse response = given().port(port)
                .header(AUTHORIZATION, authorizationHeader)
                .header("Accept","application/json")
                .param("userEmail", USER2_EMAIL)
                .post("/api/user/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);

        assertEquals(ServerResponse.OK, response);
        assertFalse(possibleUser.isPresent());
    }
}
