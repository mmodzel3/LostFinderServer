package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.authentication.AuthenticationService;
import com.github.mmodzel3.lostfinderserver.security.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.apache.commons.lang3.SystemUtils.USER_NAME;

@SpringBootTest
public class UserTestsAbstract {
    protected static final String USER_EMAIL = "test@test.com";
    protected static final String USER_PASSWORD = "test";
    protected static final String USER_NAME = "Test";
    protected static final UserRole USER_ROLE = UserRole.OWNER;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenGenerator tokenGenerator;

    protected User testUser;

    protected void createTestUser() {
        String encodedPassword = passwordEncoder.encode(USER_PASSWORD);
        testUser = new User(USER_EMAIL, encodedPassword, USER_NAME, USER_ROLE);
        String token = tokenGenerator.generate();

        testUser.setToken(token);
        userRepository.save(testUser);
    }

    protected void deleteTestUser() {
        Optional<User> userOptional = userRepository.findByEmail(USER_EMAIL);
        userOptional.ifPresent(user -> userRepository.delete(user));
    }
}
