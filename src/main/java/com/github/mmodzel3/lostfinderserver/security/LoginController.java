package com.github.mmodzel3.lostfinderserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
class LoginController {

    final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    String login(@RequestParam String email, @RequestParam String password) {
        return authenticationService.login(email, password);
    }
}
