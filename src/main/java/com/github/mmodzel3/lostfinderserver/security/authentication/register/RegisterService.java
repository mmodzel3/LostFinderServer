package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenProvider;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    User register(String email, String password, String username, UserRole role) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, username, role);

        userRepository.save(user);
        return user;
    }

    User register(String email, String password, String username) {
        return this.register(email, password, username, UserRole.USER);
    }

    public void registerIfNotExists(String email, String password, String username, UserRole role) {
        userRepository.deleteAll();

        Optional<User> possibleUser = userRepository.findByEmail(email);

        if (possibleUser.isEmpty()) {
            register(email, password, username, role);
        }
    }
}
