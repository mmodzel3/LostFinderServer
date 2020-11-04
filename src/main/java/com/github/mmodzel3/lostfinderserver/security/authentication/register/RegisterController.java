package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping
    void register(@RequestParam String email, @RequestParam String password, @RequestParam String username) throws AccountExistsException {
        registerService.register(email, password, username);
    }
}
