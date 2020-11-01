package com.github.mmodzel3.lostfinderserver;

import com.github.mmodzel3.lostfinderserver.authentication.AuthenticationService;
import com.github.mmodzel3.lostfinderserver.authentication.register.RegisterService;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialUserLoader implements ApplicationRunner {

    private static final String USER_EMAIL = "admin@owner.com";
    private static final String USER_PASSWORD = "admin";
    private static final String USER_NAME = "Owner";

    private final RegisterService registerService;

    public InitialUserLoader(RegisterService registerService) {
        this.registerService = registerService;
    }

    public void run(ApplicationArguments args) {
        registerService.registerIfNotExists(USER_EMAIL, USER_PASSWORD, USER_NAME, UserRole.OWNER);
    }
}
