package com.github.mmodzel3.lostfinderserver.security.authentication.logout;

import com.github.mmodzel3.lostfinderserver.security.authentication.login.LoginInfo;
import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LogoutController {

    private final LogoutService logoutService;

    LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/api/logout")
    ServerResponse logout(@AuthenticationPrincipal Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        logoutService.logout(user);
        return ServerResponse.OK;
    }
}
