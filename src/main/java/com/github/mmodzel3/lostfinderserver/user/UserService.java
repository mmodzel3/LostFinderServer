package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    void updateUserLocation(User user, Location location) {
        user.setLocation(location);
        userRepository.save(user);
    }
}
