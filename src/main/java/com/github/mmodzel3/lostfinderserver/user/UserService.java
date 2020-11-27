package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    void updateUserLocation(User user, Location location) {
        user.setLocation(location);
        user.setLastUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }

    void updateUserNotificationDestToken(User user, String notificationDestToken) {
        user.setNotificationDestToken(notificationDestToken);
        userRepository.save(user);
    }
}
