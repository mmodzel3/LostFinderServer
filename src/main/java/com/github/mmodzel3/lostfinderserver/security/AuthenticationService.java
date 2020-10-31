package com.github.mmodzel3.lostfinderserver.security;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        return user
                .filter((u) -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    String token = UUID.randomUUID().toString();
                    u.setToken(token);
                    userRepository.save(u);
                    return token;
                }).orElse(StringUtils.EMPTY);
    }

    Optional<AuthenticatedUserDetails> findAuthenticatedUserByToken(String token) {
        Optional<User> user = userRepository.findByToken(token);
        return user.map(AuthenticatedUserDetails::new);
    }
}
