package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
class LoginController {

    private final LoginService loginService;

    LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    @ResponseBody
    LoginInfo login(@RequestParam String email, @RequestParam String password) {
        return loginService.login(email, password);
    }
}
