package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final static String DELETED = "-DELETED";

    @Value("${user.min.password.length:8}")
    int minPasswordLength;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getExistingUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isDeleted())
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
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

    public void updateUserLocation(User user, Location location) {
        user.setLocation(location);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public void clearLoggedUserData(User user) {
        user.setLocation(null);
        user.setNotificationDestToken(null);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public void updateUserLoginDateToNow(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginDate(now);
        user.setLastUpdateDate(now);

        userRepository.save(user);
    }

    boolean updateUserPassword(User user, String oldPassword, String newPassword) {
        if (passwordEncoder.matches(oldPassword, user.getPassword()) && newPassword.length() >= minPasswordLength) {
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
            user.setLastUpdateDate(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new UserUpdatePermissionException();
        }
    }

    void updateUserBlock(User userChanging, String userToChangeEmail, boolean isBlocked)
            throws UserUpdatePermissionException, UserNotFoundException {
        Optional<User> possibleUser = findUserByEmail(userToChangeEmail);
        User user = possibleUser.orElseThrow(UserNotFoundException::new);

        if (userChanging.isMorePrivileged(user)){
            user.setBlocked(isBlocked);
            user.setLastUpdateDate(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new UserUpdatePermissionException();
        }
    }

    void deleteUser(User userChanging, String userToDeleteEmail)
            throws UserUpdatePermissionException, UserNotFoundException {
        Optional<User> possibleUser = findUserByEmail(userToDeleteEmail);
        User user = possibleUser.orElseThrow(UserNotFoundException::new);

        if (userChanging.isMorePrivileged(user) || userChanging.getEmail().equals(user.getEmail())){
            String uuid = UUID.randomUUID().toString();
            String deletedUsernamePostfix = " [" + DELETED + " " + uuid + "]";
            String deletedEmailPostfix = DELETED + uuid;

            user.setUsername(user.getUsername() + deletedUsernamePostfix);
            user.setEmail(user.getEmail() + deletedEmailPostfix);
            user.setNotificationDestToken(null);

            user.setBlocked(true);
            user.setDeleted(true);
            user.setLastUpdateDate(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new UserUpdatePermissionException();
        }
    }
}
