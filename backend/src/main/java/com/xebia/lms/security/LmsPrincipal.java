/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.security;

import com.xebia.lms.domain.AppUser;
import com.xebia.lms.domain.enums.UserStatus;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Principal object representing the authenticated user within the Security Context.
 *
 * Implements Spring Security's {@link UserDetails} interface, containing core
 * identity claims (userId, roleId, permissionVersion) used for authorization checks
 * and cache mappings.
 */
@Getter
@RequiredArgsConstructor
public class LmsPrincipal implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String password;
    private final String displayName;
    private final UUID roleId;
    private final UUID organisationId;
    private final Integer permissionVersion;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method to build an LmsPrincipal from an {@link AppUser} domain entity
     * and a collection of mapped Spring Security authorities.
     *
     * @param user the AppUser entity
     * @param authorities the resolved authorities for the user
     * @return the constructed LmsPrincipal instance
     */
    public static LmsPrincipal build(AppUser user, Collection<? extends GrantedAuthority> authorities) {
        return new LmsPrincipal(
            user.getUserId(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getDisplayName(),
            user.getRoleId(),
            user.getOrgId(),
            user.getPermissionVersion(),
            user.getStatus(),
            authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}

