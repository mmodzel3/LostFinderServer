package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.security.authentication.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class AuthenticatedUserTestsAbstract extends UserTestsAbstract {
    protected final String AUTHROIZATION = "Authorization";
    protected final String BEARER = "Bearer";

    @Autowired
    LoginService loginService;

    protected String token;
    protected String authorizationHeader;

    @Override
    protected void createTestUser() {
        super.createTestUser();
        loginTestUser();
    }

    private void loginTestUser() {
        token = loginService.login(USER_EMAIL, USER_PASSWORD).token;
        authorizationHeader = BEARER + " " + token;
    }
}
