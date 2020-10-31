package com.github.mmodzel3.lostfinderserver;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialUserLoader implements ApplicationRunner {

    private static final String USER_EMAIL = "admin@owner.com";
    private static final String USER_PASSWORD = "admin";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void run(ApplicationArguments args) {
        Optional<User> owner = userRepository.findByEmail(USER_EMAIL);

        if (owner.isEmpty()) {
            User user = createInitialUser();
            userRepository.save(user);
        }
    }

    private User createInitialUser() {
        return new User(USER_EMAIL, passwordEncoder.encode(USER_PASSWORD));
    }
}
