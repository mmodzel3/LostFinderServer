package com.github.mmodzel3.lostfinderserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping
    String register(@RequestParam String email, @RequestParam String password, @RequestParam String username) {
        return authenticationService.register(email, password, username);
    }
}
