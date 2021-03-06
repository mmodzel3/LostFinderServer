package com.github.mmodzel3.lostfinderserver.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.mmodzel3.lostfinderserver.location.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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

    @Indexed(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Indexed(unique = true)
    private String username;

    private Location location;

    private UserRole role;

    private boolean isBlocked;

    private boolean isDeleted;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String notificationDestToken;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdateDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastLoginDate;

    public User() {

    }

    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.isBlocked = false;
        this.isDeleted = false;
        this.lastUpdateDate = LocalDateTime.now();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleString = ROLE_PREFIX + role.toString();
        return Collections.singletonList(new SimpleGrantedAuthority(roleString));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !isDeleted;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return !isDeleted;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !isBlocked;
    }

    @JsonIgnore
    public boolean isManager() {
        return role.equals(UserRole.MANAGER) || role.equals(UserRole.OWNER);
    }

    @JsonIgnore
    public boolean isOwner() {
        return role.equals(UserRole.OWNER);
    }

    @JsonIgnore
    public boolean isMorePrivileged(User user) {
        if (role.equals(UserRole.MANAGER) && user.role.equals(UserRole.USER)) {
            return true;
        } else return role.equals(UserRole.OWNER) && !user.role.equals(UserRole.OWNER);
    }
}
