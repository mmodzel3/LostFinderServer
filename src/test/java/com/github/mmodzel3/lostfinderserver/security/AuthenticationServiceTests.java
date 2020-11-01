package com.github.mmodzel3.lostfinderserver.security;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthenticationServiceTests {

    private static final String USER_EMAIL = "test@test.com";
    private static final String USER_PASSWORD = "test";
    private static final String USER_NAME = "Test";
    private static final UserRole USER_ROLE = UserRole.OWNER;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private User testUser;

    @Test
    void whenLoginToExistingUserThenGotToken() {
        createTestUser();
        String token = authenticationService.login(USER_EMAIL, USER_PASSWORD);
        deleteTestUser();

        assertNotEquals(StringUtils.EMPTY, token);
    }

    void createTestUser() {
        String encodedPassword = passwordEncoder.encode(USER_PASSWORD);
        testUser = new User(USER_EMAIL, encodedPassword, USER_NAME, USER_ROLE);
        userRepository.save(testUser);
    }

    void deleteTestUser() {
        Optional<User> userOptional = userRepository.findByEmail(USER_EMAIL);
        userOptional.ifPresent(user -> userRepository.delete(user));
    }

    @Test
    void whenFindAuthenticatedUserThenGotDetails() {
        createTestUser();
        String token = authenticationService.login(USER_EMAIL, USER_PASSWORD);
        Optional<AuthenticatedUserDetails> authenticatedUser = authenticationService.findAuthenticatedUserByToken(token);
        deleteTestUser();

        assertTrue(authenticatedUser.isPresent());
        assertEquals(USER_EMAIL, authenticatedUser.get().getUsername());
    }

    @Test
    void whenUserRegisteredThenUserIsPresent() {
        authenticationService.register(USER_EMAIL, USER_PASSWORD, USER_NAME, USER_ROLE);

        Optional<User> userOptional = userRepository.findByEmail(USER_EMAIL);
        deleteTestUser();

        assertTrue(userOptional.isPresent());
        assertEquals(USER_NAME, userOptional.get().getUsername());
        assertEquals(USER_ROLE, userOptional.get().getRole());
    }
}
