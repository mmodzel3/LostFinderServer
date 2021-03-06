package com.github.mmodzel3.lostfinderserver.security.authentication.register;

import com.github.mmodzel3.lostfinderserver.server.ServerResponse;
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
    ServerResponse register(@RequestParam String email, @RequestParam String password,
                            @RequestParam(defaultValue = "") String serverPassword, @RequestParam String username) {
        try {
            registerService.register(email, password, serverPassword, username);
            return ServerResponse.OK;
        } catch (AccountExistsException e) {
            return ServerResponse.DUPLICATED;
        } catch (InvalidServerPasswordException e) {
            return ServerResponse.INVALID_PERMISSION;
        } catch (InvalidRegisterParamsException e) {
            return ServerResponse.INVALID_PARAM;
        }
    }
}
