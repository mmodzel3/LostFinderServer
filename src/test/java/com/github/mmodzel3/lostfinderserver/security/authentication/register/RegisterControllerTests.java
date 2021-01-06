package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerTests extends UserTestsAbstract {
    private static final String USER_EMAIL2 = "test2@test.com";
    private static final String USER_NAME2 = "Test2";
    private static final String SERVER_PASSWORD = "1234";
    private static final String WRONG_SERVER_PASSWORD = "123!!!";

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    RegisterService registerService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        registerService.serverPassword = SERVER_PASSWORD;
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenRegisterNotExistingUserThenGotRegistered() {
        given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("serverPassword", SERVER_PASSWORD)
                .param("username", USER_NAME)
                .post("register")
                .then()
                .statusCode(200);

        Optional<User> possibleRegisteredUser = userService.findUserByEmail(USER_EMAIL);
        assertTrue(possibleRegisteredUser.isPresent());
        assertTrue(passwordEncoder.matches(USER_PASSWORD, possibleRegisteredUser.get().getPassword()));
        assertEquals(USER_NAME, possibleRegisteredUser.get().getUsername());
    }

    @Test
    void whenRegisterDuplicatedUserWithSameEmailThenItIsNotRegistered() {
        createTestUser();

        ServerResponse response = given().port(port)
                .param("email", USER_EMAIL)
                .param("password", USER_PASSWORD)
                .param("serverPassword", SERVER_PASSWORD)
                .param("username", USER_NAME2)
                .post("register")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.DUPLICATED, response);
    }

    @Test
    void whenRegisterDuplicatedUserWithSameUsernameThenItIsNotRegistered() {
        createTestUser();

        ServerResponse response = given().port(port)
                .param("email", USER_EMAIL2)
                .param("password", USER_PASSWORD)
                .param("serverPassword", SERVER_PASSWORD)
                .param("username", USER_NAME)
                .post("register")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.DUPLICATED, response);
    }

    @Test
    void whenRegisterWithWrongServerPasswordThenItIsNotRegistered() {
        createTestUser();

        ServerResponse response = given().port(port)
                .param("email", USER_EMAIL2)
                .param("password", USER_PASSWORD)
                .param("serverPassword", WRONG_SERVER_PASSWORD)
                .param("username", USER_NAME)
                .post("register")
                .then()
                .statusCode(200)
                .extract()
                .as(ServerResponse.class);

        assertEquals(ServerResponse.INVALID_PERMISSION, response);
    }
}
