package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public void updateUserNotificationDestTokenByEmail(String email, String notificationDestToken) {
        findUserByEmail(email).ifPresent(u -> updateUserNotificationDestToken(u, notificationDestToken));
    }

    public void updateUserNotificationDestToken(User user, String notificationDestToken) {
        if (notificationDestToken != null) {
            userRepository.findAllByNotificationDestToken(notificationDestToken).stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .forEach(u -> updateUserNotificationDestToken(u, null));
        }

        user.setNotificationDestToken(notificationDestToken);
        userRepository.save(user);
    }

    public void updateUserLoginDateToNow(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginDate(now);
        user.setLastUpdateDate(now);

        userRepository.save(user);
    }

    boolean updateUserPassword(User user, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(newPassword);

            user.setPassword(encodedPassword);
            user.setLastUpdateDate(LocalDateTime.now());
            userRepository.save(user);

            return true;
        } else {
            return false;
        }
    }

    void updateUserRole(User userChanging, String userToChangeEmail, UserRole userRole)
            throws UserUpdatePermissionException, UserNotFoundException {
        if (userChanging.isOwner()){
            Optional<User> possibleUser = findUserByEmail(userToChangeEmail);
            User user = possibleUser.orElseThrow(UserNotFoundException::new);

            user.setRole(userRole);
            userRepository.save(user);
        } else {
            throw new UserUpdatePermissionException();
        }
    }

    void updateUserLocation(User user, Location location) {
        user.setLocation(location);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }
}
