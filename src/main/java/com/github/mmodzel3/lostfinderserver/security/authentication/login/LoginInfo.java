package com.github.mmodzel3.lostfinderserver.security.authentication.login;

import com.github.mmodzel3.lostfinderserver.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private String token;
    private UserRole role;
}
