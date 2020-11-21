package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenDetails;
import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenProvider;
import com.github.mmodzel3.lostfinderserver.user.User;
import com.github.mmodzel3.lostfinderserver.user.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginInfo login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        String token = user.filter((u) -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> tokenProvider.generateToken(new TokenDetails(u.getEmail())))
                .orElse(StringUtils.EMPTY);

        return new LoginInfo(token);
    }
}
