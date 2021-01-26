package com.github.mmodzel3.lostfinderserver.security.authentication;

import com.github.mmodzel3.lostfinderserver.security.authentication.token.InvalidTokenException;
import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenDetails;
import com.github.mmodzel3.lostfinderserver.security.authentication.token.TokenProvider;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

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
                .flatMap(this::findUserByToken)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + token));
    }

    private Optional<User> findUserByToken(String token) {
        try {
            TokenDetails tokenDetails = tokenProvider.parseToken(token);
            String userEmail = tokenDetails.getUserEmail();

            return authenticationService.findUserByEmail(userEmail);
        } catch (InvalidTokenException e) {
            return Optional.empty();
        }
    }
}
