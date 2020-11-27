package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenDetails;
import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenProvider;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    LoginService(UserService userService, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginInfo login(String email, String password) {
        Optional<User> possibleUser = userService.findUserByEmail(email);

        String token = possibleUser.filter((u) -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> tokenProvider.generateToken(new TokenDetails(u.getEmail())))
                .orElse(StringUtils.EMPTY);

        return new LoginInfo(token);
    }

    public LoginInfo login(String email, String password, String notificationDestToken) {
        LoginInfo loginInfo = login(email, password);

        if (!loginInfo.getToken().isEmpty()) {
            userService.updateUserNotificationDestTokenByEmail(email, notificationDestToken);
        }

        return loginInfo;
    }
}
