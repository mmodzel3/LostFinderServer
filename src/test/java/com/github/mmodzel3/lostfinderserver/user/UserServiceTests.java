package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTests extends UserTestsAbstract {
    private final int ONE_ELEMENT_LIST_SIZE = 1;

    private final double TEST_LATITUDE = 20.2;
    private final double TEST_LONGITUDE = 23.2;

    private final String TEST_NOTIFICATION_DEST_TOKEN = "token";

    private final int TWO_ELEMENT_LIST_SIZE = 2;
    private final String USER2_EMAIL = "user2@example.com";
    private final String USER2_NAME = "user2";

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        deleteAllUsers();
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteAllUsers();
    }

    @Test
    void whenFindUserByEmailThenGotIt() {
        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);

        assertTrue(possibleUser.isPresent());
        assertEquals(USER_EMAIL, possibleUser.get().getEmail());
    }

    @Test
    void whenGetAllUsersThenGotAll() {
        List<User> users = userService.getAllUsers();

        assertEquals(ONE_ELEMENT_LIST_SIZE, users.size());
        assertEquals(USER_EMAIL, users.get(0).getEmail());
    }

    @Test
    void whenAddUserThenItIsAdded() {
        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, USER_ROLE);

        userService.addUser(user);
        List<User> users = userRepository.findAll();
        List<String> usersEmails = users.stream().map(User::getEmail).collect(Collectors.toList());

        assertEquals(TWO_ELEMENT_LIST_SIZE, users.size());
        assertTrue(usersEmails.contains(USER2_EMAIL));
    }

    @Test
    void whenUpdateUserLocationThenItIsUpdated() {
        Location location = new Location(TEST_LATITUDE, TEST_LONGITUDE);

        userService.updateUserLocation(testUser, location);

        Optional<User> possibleUser = userRepository.findByEmail(testUser.getEmail());
        assertTrue(possibleUser.isPresent());
        assertEquals(TEST_LATITUDE, possibleUser.get().getLocation().getLatitude());
        assertEquals(TEST_LONGITUDE, possibleUser.get().getLocation().getLongitude());
        assertNotEquals(testUser.getLastUpdateDate(), possibleUser.get().getLastUpdateDate());
    }

    @Test
    void whenUpdateUserNotificationDestTokenThenItIsUpdated() {
        userService.updateUserNotificationDestToken(testUser, TEST_NOTIFICATION_DEST_TOKEN);

        Optional<User> possibleUser = userRepository.findByEmail(testUser.getEmail());
        assertTrue(possibleUser.isPresent());
        assertEquals(TEST_NOTIFICATION_DEST_TOKEN, possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenUpdateUserNotificationDestTokenAndItWasUsedByOtherAccountThenItIsRemoved() {
        User user = new User(USER2_EMAIL, USER_PASSWORD, USER2_NAME, USER_ROLE);
        user.setNotificationDestToken(TEST_NOTIFICATION_DEST_TOKEN);
        userRepository.save(user);

        userService.updateUserNotificationDestToken(testUser, TEST_NOTIFICATION_DEST_TOKEN);

        Optional<User> possibleUser = userRepository.findByEmail(USER2_EMAIL);
        assertTrue(possibleUser.isPresent());
        assertNull(possibleUser.get().getNotificationDestToken());
    }

    @Test
    void whenUpdateUserNotificationDestTokenByEmailThenItIsUpdated() {
        userService.updateUserNotificationDestTokenByEmail(testUser.getEmail(), TEST_NOTIFICATION_DEST_TOKEN);

        Optional<User> possibleUser = userRepository.findByEmail(testUser.getEmail());
        assertTrue(possibleUser.isPresent());
        assertEquals(TEST_NOTIFICATION_DEST_TOKEN, possibleUser.get().getNotificationDestToken());
    }
}
