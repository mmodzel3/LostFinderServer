package com.github.mmodzel3.lostfinderserver.authentication;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<AuthenticatedUserDetails> findAuthenticatedUserByToken(String token) {
        Optional<User> user = userRepository.findByToken(token);
        return user.map(AuthenticatedUserDetails::new);
    }
}
