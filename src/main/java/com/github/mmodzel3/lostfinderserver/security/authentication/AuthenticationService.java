package com.github.mmodzel3.lostfinderserver.security.authentication;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class AuthenticationService {

    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> findUserByEmail(String email) {
        return userService.findUserByEmail(email);
    }
}
