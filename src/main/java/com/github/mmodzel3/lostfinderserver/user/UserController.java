package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.github.mmodzel3.lostfinderserver.server.ServerResponse.OK;

@RestController
public class UserController {

    final private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/users")
    private List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/api/user/location")
    private ServerResponse updateUserLocation(@AuthenticationPrincipal Authentication authentication,
                                              @RequestBody Location location) {
        User user = (User) authentication.getPrincipal();
        user.setLocation(location);
        userRepository.save(user);

        return OK;
    }
}
