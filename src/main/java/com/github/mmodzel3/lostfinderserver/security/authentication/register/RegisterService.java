package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    RegisterService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    User register(String email, String password, String username, UserRole role) throws AccountExistsException {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, username, role);

        try {
            userService.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new AccountExistsException();
        }

        return user;
    }

    User register(String email, String password, String username) throws AccountExistsException {
        return register(email, password, username, UserRole.USER);
    }

    public boolean registerIfNotExists(String email, String password, String username, UserRole role) {
        try {
            register(email, password, username, role);
            return true;
        } catch (AccountExistsException e) {
            return false;
        }
    }
}
