package com.microservice.notification.notification_service.configs;

import com.microservice.notification.notification_service.entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom implementation of {@link UserDetails} to integrate with the application's user entity.
 * <p>
 * This class adapts {@link UserInfo} to the Spring Security {@link UserDetails} interface,
 * allowing it to be used for authentication and authorization.
 * </p>
 */
public class UserInfoUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    /**
     * Constructs a {@code UserInfoUserDetails} instance from a {@link UserInfo} object.
     *
     * @param userInfo the {@link UserInfo} object representing the user.
     */
    public UserInfoUserDetails(UserInfo userInfo) {
        this.username = userInfo.getName();
        this.password = userInfo.getPassword();
        this.authorities = Arrays.stream(userInfo.getRole().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of {@link GrantedAuthority} objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the user's password.
     *
     * @return the password as a {@code String}.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's username.
     *
     * @return the username as a {@code String}.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account is expired.
     *
     * @return {@code true} if the account is not expired, otherwise {@code false}.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     *
     * @return {@code true} if the account is not locked, otherwise {@code false}.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials are expired.
     *
     * @return {@code true} if the credentials are not expired, otherwise {@code false}.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return {@code true} if the user is enabled, otherwise {@code false}.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
