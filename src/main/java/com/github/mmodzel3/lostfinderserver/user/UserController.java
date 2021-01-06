package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.github.mmodzel3.lostfinderserver.server.ServerResponse.*;

@RestController
public class UserController {

    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    private List<User> getUsers(@RequestParam(required = false) boolean all) {
        if (all) {
            return userService.getAllUsers();
        } else {
            return userService.getExistingUsers();
        }
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
    private ServerResponse updateUserRole(@AuthenticationPrincipal Authentication authentication,
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

    @PostMapping("/api/user/block")
    private ServerResponse updateUserRole(@AuthenticationPrincipal Authentication authentication,
                                          @RequestParam String userEmail,
                                          @RequestParam boolean isBlocked) {
        User user = (User) authentication.getPrincipal();

        try {
            userService.updateUserBlock(user, userEmail, isBlocked);
            return OK;
        } catch (UserNotFoundException e) {
            return NOT_FOUND;
        } catch (UserUpdatePermissionException e) {
            return INVALID_PERMISSION;
        }
    }

    @PostMapping("/api/user/delete")
    private ServerResponse deleteUser(@AuthenticationPrincipal Authentication authentication,
                                          @RequestParam(defaultValue = StringUtils.EMPTY) String userEmail) {
        User user = (User) authentication.getPrincipal();

        try {
            if (!userEmail.isEmpty()) {
                userService.deleteUser(user, userEmail);
            } else {
                userService.deleteUser(user, user.getEmail());
            }

            return OK;
        } catch (UserNotFoundException e) {
            return NOT_FOUND;
        } catch (UserUpdatePermissionException e) {
            return INVALID_PERMISSION;
        }
    }
}
