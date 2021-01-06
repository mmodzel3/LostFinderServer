package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${register.server.password:''}")
    String serverPassword;

    RegisterService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    User register(String email, String password, String serverPassword, String username, UserRole role)
            throws AccountExistsException, InvalidServerPasswordException {
        if (!this.serverPassword.isEmpty() && !this.serverPassword.equals(serverPassword)) {
            throw new InvalidServerPasswordException();
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, username, role);

        try {
            userService.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new AccountExistsException();
        }

        return user;
    }

    User register(String email, String password, String serverPassword, String username)
            throws AccountExistsException, InvalidServerPasswordException {
        return register(email, password, serverPassword, username, UserRole.USER);
    }

    public boolean registerIfNotExists(String email, String password, String username, UserRole role) {
        try {
            register(email, password, username, serverPassword, role);
            return true;
        } catch (AccountExistsException | InvalidServerPasswordException e) {
            return false;
        }
    }
}
