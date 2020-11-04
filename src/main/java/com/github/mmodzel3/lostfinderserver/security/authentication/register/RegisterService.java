package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import com.github.mmodzel3.lostfinderserver.user.UserRole;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    User register(String email, String password, String username, UserRole role) throws AccountExistsException {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, username, role);

        try {
            userRepository.save(user);
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
