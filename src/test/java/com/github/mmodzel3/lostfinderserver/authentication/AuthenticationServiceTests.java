package com.github.mmodzel3.lostfinderserver.authentication;

import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthenticationServiceTests extends UserTestsAbstract {
     @Autowired
    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        createTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenFindAuthenticatedUserThenGotDetails() {
        String token = testUser.getToken();
        Optional<AuthenticatedUserDetails> authenticatedUser = authenticationService.findAuthenticatedUserByToken(token);

        assertTrue(authenticatedUser.isPresent());
        assertEquals(testUser.getEmail(), authenticatedUser.get().getUsername());
    }
}
