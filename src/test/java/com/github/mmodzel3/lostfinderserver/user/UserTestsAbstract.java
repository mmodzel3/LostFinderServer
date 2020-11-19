package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootTest
public abstract class UserTestsAbstract {
    protected static final String USER_EMAIL = "test@test.com";
    protected static final String USER_PASSWORD = "test";
    protected static final String USER_NAME = "Test";
    protected static final UserRole USER_ROLE = UserRole.OWNER;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenProvider tokenProvider;

    protected User testUser;

    protected void createTestUser() {
        String encodedPassword = passwordEncoder.encode(USER_PASSWORD);
        testUser = new User(USER_EMAIL, encodedPassword, USER_NAME, USER_ROLE);

        userRepository.save(testUser);
    }

    protected void deleteTestUser() {
        Optional<User> userOptional = userRepository.findByEmail(USER_EMAIL);
        userOptional.ifPresent(user -> userRepository.delete(user));
    }

    protected void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
