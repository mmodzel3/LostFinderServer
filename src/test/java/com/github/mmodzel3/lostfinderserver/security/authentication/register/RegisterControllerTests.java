package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerTests extends UserTestsAbstract {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenRegisterNotExistingUserThenGotRegistered() {
        given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("username", USER_NAME)
                .post("register")
                .then()
                .statusCode(200);

        Optional<User> possibleRegisteredUser = userRepository.findByEmail(USER_EMAIL);
        assertTrue(possibleRegisteredUser.isPresent());
        assertTrue(passwordEncoder.matches(USER_PASSWORD, possibleRegisteredUser.get().getPassword()));
        assertEquals(USER_NAME, possibleRegisteredUser.get().getUsername());
    }

    @Test
    void whenRegisterDuplicatedUserThenItIsNotRegistered() {
        createTestUser();

        given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("username", USER_NAME)
                .post("register")
                .then()
                .statusCode(500);
    }
}
