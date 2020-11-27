package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterServiceTests extends UserTestsAbstract {

    @Autowired
    RegisterService registerService;

    @Autowired
    UserService userService;

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenRegisterUserThenUserIsRegistered() throws AccountExistsException {
        registerService.register(USER_EMAIL, USER_PASSWORD, USER_NAME, USER_ROLE);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);

        assertTrue(possibleUser.isPresent());
        assertEquals(USER_NAME, possibleUser.get().getUsername());
        assertEquals(USER_ROLE, possibleUser.get().getRole());
    }

    @Test
    void whenRegisterDuplicatedUserThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(AccountExistsException.class, () ->
                registerService.register(USER_EMAIL, USER_PASSWORD, USER_NAME, USER_ROLE));
    }
}
