package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;

import static com.github.mmodzel3.lostfinderserver.server.ServerResponse.*;

@RestController
public class UserController {

    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    private List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/api/user/location")
    private ServerResponse updateUserLocation(@AuthenticationPrincipal Authentication authentication,
                                              @RequestBody Location location) {
        User user = (User) authentication.getPrincipal();
        userService.updateUserLocation(user, location);

        return OK;
    }

    @PostMapping("/api/user/notification/token")
    private ServerResponse updateUserNotificationDestToken(@AuthenticationPrincipal Authentication authentication,
                                                           @RequestParam String token) {
        User user = (User) authentication.getPrincipal();
        userService.updateUserNotificationDestToken(user, token);

        return OK;
    }

    @PostMapping("/api/user/password")
    private ServerResponse updateUserPassword(@AuthenticationPrincipal Authentication authentication,
                                              @RequestParam String oldPassword,
                                              @RequestParam String newPassword) {
        User user = (User) authentication.getPrincipal();
        if (userService.updateUserPassword(user, oldPassword, newPassword)) {
            return OK;
        } else {
            return INVALID_PARAM;
        }
    }

    @PostMapping("/api/user/role")
    private ServerResponse updateUserPassword(@AuthenticationPrincipal Authentication authentication,
                                              @RequestParam String userEmail,
                                              @RequestParam UserRole role) {
        User user = (User) authentication.getPrincipal();

        try {
            userService.updateUserRole(user, userEmail, role);
            return OK;
        } catch (UserNotFoundException e) {
            return NOT_FOUND;
        } catch (UserUpdatePermissionException e) {
            return INVALID_PERMISSION;
        }
    }
}
