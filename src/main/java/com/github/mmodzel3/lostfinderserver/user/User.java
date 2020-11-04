package com.github.mmodzel3.lostfinderserver.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Document(collection = "users")
public class User implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @Setter(AccessLevel.NONE)
    @Indexed(unique = true)
    private String email;

    private String password;

    private String username;

    private Double longitude;
    private Double latitude;

    private UserRole role;

    public User() {

    }

    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleString = ROLE_PREFIX + role.toString();
        System.out.println(roleString);
        return Collections.singletonList(new SimpleGrantedAuthority(roleString));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}