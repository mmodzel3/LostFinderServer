package com.github.mmodzel3.lostfinderserver.authentication.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
class LoginController {

    private final LoginService loginService;

    LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    String login(@RequestParam String email, @RequestParam String password) {
        return loginService.login(email, password);
    }
}
