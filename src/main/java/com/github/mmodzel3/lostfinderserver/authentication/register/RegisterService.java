package com.github.mmodzel3.lostfinderserver.authentication.register;

import com.github.mmodzel3.lostfinderserver.security.TokenGenerator;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    String register(String email, String password, String username, UserRole role) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, username, role);
        String token = tokenGenerator.generate();

        user.setToken(token);
        userRepository.save(user);

        return token;
    }

    String register(String email, String password, String username) {
        return this.register(email, password, username, UserRole.USER);
    }

    public String registerIfNotExists(String email, String password, String username, UserRole role) {
        Optional<User> possibleUser = userRepository.findByEmail(email);

        if (possibleUser.isEmpty()) {
            return register(email, password, username, role);
        }

        return StringUtils.EMPTY;
    }
}
