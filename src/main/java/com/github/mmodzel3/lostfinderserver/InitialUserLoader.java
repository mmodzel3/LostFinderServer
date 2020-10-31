package com.github.mmodzel3.lostfinderserver;

import com.github.mmodzel3.lostfinderserver.security.AuthenticationService;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialUserLoader implements ApplicationRunner {

    private static final String USER_EMAIL = "admin@owner.com";
    private static final String USER_PASSWORD = "admin";
    private static final String USER_NAME = "Owner";

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public InitialUserLoader(UserRepository userRepository, AuthenticationService authenticationService,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        Optional<User> owner = userRepository.findByEmail(USER_EMAIL);

        if (owner.isEmpty()) {
            createInitialUser();
        }
    }

    private void createInitialUser() {
        authenticationService.register(USER_EMAIL, USER_PASSWORD, USER_NAME, UserRole.OWNER);
    }
}
