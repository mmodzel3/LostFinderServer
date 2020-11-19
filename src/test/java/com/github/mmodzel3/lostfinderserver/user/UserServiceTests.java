package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTests extends UserTestsAbstract {
    private final int ONE_ELEMENT_LIST_SIZE = 1;

    private final double TEST_LATITUDE = 20.2;
    private final double TEST_LONGITUDE = 23.2;

    @Autowired
    UserService userService;

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
        List<User> users = userService.getAllUsers();

        assertEquals(ONE_ELEMENT_LIST_SIZE, users.size());
        assertEquals(USER_EMAIL, users.get(0).getEmail());
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
}
