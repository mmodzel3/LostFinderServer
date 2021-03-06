package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RegisterService {
    private final static Pattern EMAIL_REGEX =
            Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${register.server.password:}")
    String serverPassword;

    @Value("${user.min.password.length:8}")
    int minPasswordLength;

    RegisterService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    User register(String email, String password, String serverPassword, String username, UserRole role)
            throws AccountExistsException, InvalidServerPasswordException, InvalidRegisterParamsException {
        if (email.length() == 0 || password.length() < minPasswordLength || username.length() == 0 || !isEmailValid(email)) {
            throw new InvalidRegisterParamsException();
        }

        if (!this.serverPassword.isEmpty() && !this.serverPassword.equals(serverPassword)) {
            throw new InvalidServerPasswordException();
        }

        email = email.toLowerCase();
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
            throws AccountExistsException, InvalidServerPasswordException, InvalidRegisterParamsException {
        return register(email, password, serverPassword, username, UserRole.USER);
    }

    public boolean registerIfNotExists(String email, String password, String username, UserRole role) {
        try {
            register(email, password, serverPassword, username, role);
            return true;
        } catch (AccountExistsException | InvalidServerPasswordException | InvalidRegisterParamsException e) {
            return false;
        }
    }

    private boolean isEmailValid(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }
}
