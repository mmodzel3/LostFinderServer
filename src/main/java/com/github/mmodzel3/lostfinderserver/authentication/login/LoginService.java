package com.github.mmodzel3.lostfinderserver.authentication.login;

import com.github.mmodzel3.lostfinderserver.security.TokenGenerator;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        return user
                .filter((u) -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    String token = tokenGenerator.generate();
                    u.setToken(token);
                    userRepository.save(u);
                    return token;
                }).orElse(StringUtils.EMPTY);
    }
}
