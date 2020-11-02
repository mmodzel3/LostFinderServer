package com.github.mmodzel3.lostfinderserver.security;

import com.github.mmodzel3.lostfinderserver.authentication.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final AuthenticationService authenticationService;
    private final TokenProvider tokenProvider;

    public AuthenticationProvider(AuthenticationService authenticationService, TokenProvider tokenProvider) {
        this.authenticationService = authenticationService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        Object token = authentication.getCredentials();
        return Optional
                .ofNullable(token)
                .map(String::valueOf)
                .map(tokenProvider::parseToken)
                .map(TokenDetails::getUserEmail)
                .flatMap(authenticationService::findUserByEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
    }
}
