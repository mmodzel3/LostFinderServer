package com.github.mmodzel3.lostfinderserver.authentication;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
