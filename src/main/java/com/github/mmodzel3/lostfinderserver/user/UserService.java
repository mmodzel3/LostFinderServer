package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public
class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
