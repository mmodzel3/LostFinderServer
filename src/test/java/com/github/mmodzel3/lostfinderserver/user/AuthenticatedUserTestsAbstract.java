package com.github.mmodzel3.lostfinderserver.user;

import com.github.mmodzel3.lostfinderserver.security.authentication.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class AuthenticatedUserTestsAbstract extends UserTestsAbstract {
    protected static final String AUTHROIZATION = "Authorization";
    protected static final String BEARER = "Bearer";
    protected static final String USER_NOTIFICATION_DEST_TOKEN = "notification_token";

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
        token = loginService.login(USER_EMAIL, USER_PASSWORD, USER_NOTIFICATION_DEST_TOKEN).getToken();
        authorizationHeader = BEARER + " " + token;
    }
}
