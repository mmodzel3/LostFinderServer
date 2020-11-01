package com.github.mmodzel3.lostfinderserver.authentication.register;

import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    String register(@RequestParam String email, @RequestParam String password, @RequestParam String username) {
        return registerService.register(email, password, username);
    }
}
