package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import com.github.mmodzel3.lostfinderserver.user.UserTestsAbstract;
import io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterServiceTests extends UserTestsAbstract {
    private static final String USER_EMAIL2 = "test2@test.com";
    private static final String USER_NAME2 = "Test2";
    private static final String USER_INVALID_EMAIL = "invalid_email";
    private static final String SERVER_PASSWORD = "12345678";
    private static final String WRONG_SERVER_PASSWORD = "12345678!!!";
    private static final String TOO_SHORT_PASSWORD = "1";
    private static final int SERVER_MIN_PASSWORD_LENGTH = 2;

    @Autowired
    RegisterService registerService;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        registerService.serverPassword = SERVER_PASSWORD;
        registerService.minPasswordLength = SERVER_MIN_PASSWORD_LENGTH;
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void whenRegisterUserThenUserIsRegistered()
            throws AccountExistsException, InvalidServerPasswordException, InvalidRegisterParamsException {
        registerService.register(USER_EMAIL, USER_PASSWORD, SERVER_PASSWORD, USER_NAME, USER_ROLE);

        Optional<User> possibleUser = userService.findUserByEmail(USER_EMAIL);

        assertTrue(possibleUser.isPresent());
        assertEquals(USER_NAME, possibleUser.get().getUsername());
        assertEquals(USER_ROLE, possibleUser.get().getRole());
    }

    @Test
    void whenRegisterDuplicatedUserWithSameEmailThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(AccountExistsException.class, () ->
                registerService.register(USER_EMAIL, USER_PASSWORD, SERVER_PASSWORD, USER_NAME2, USER_ROLE));
    }

    @Test
    void whenRegisterDuplicatedUserWithSameUsernameThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(AccountExistsException.class, () ->
                registerService.register(USER_EMAIL2, USER_PASSWORD, SERVER_PASSWORD, USER_NAME, USER_ROLE));
    }

    @Test
    void whenRegisterWithWrongServerPasswordThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidServerPasswordException.class, () ->
                registerService.register(USER_EMAIL, USER_PASSWORD, WRONG_SERVER_PASSWORD, USER_NAME2, USER_ROLE));
    }

    @Test
    void whenRegisterWithBlankEmailThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidRegisterParamsException.class, () ->
                registerService.register(StringUtil.EMPTY_STRING, USER_PASSWORD, SERVER_PASSWORD, USER_NAME2, USER_ROLE));
    }

    @Test
    void whenRegisterWithBlankUsernameThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidRegisterParamsException.class, () ->
                registerService.register(USER_EMAIL, USER_PASSWORD, SERVER_PASSWORD, StringUtil.EMPTY_STRING, USER_ROLE));
    }

    @Test
    void whenRegisterWithTooShortPasswordThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidRegisterParamsException.class, () ->
                registerService.register(USER_EMAIL, TOO_SHORT_PASSWORD, SERVER_PASSWORD, StringUtil.EMPTY_STRING, USER_ROLE));
    }

    @Test
    void whenRegisterWithTooShortThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidRegisterParamsException.class, () ->
                registerService.register(USER_EMAIL, USER_PASSWORD, SERVER_PASSWORD, StringUtil.EMPTY_STRING, USER_ROLE));
    }

    @Test
    void whenRegisterWithInvalidEmailThenUserIsNotRegistered() {
        createTestUser();

        assertThrows(InvalidRegisterParamsException.class, () ->
                registerService.register(USER_INVALID_EMAIL, USER_PASSWORD, SERVER_PASSWORD, USER_NAME2, USER_ROLE));
    }
}
