// src/main/java/com/derinkaras/aimsense/security/CustomUserPrincipal.java
package com.derinkaras.aimsense.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final String userId;  // Supabase `sub`
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(String userId,
                               String email,
                               Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    // UserDetails stuff – you can just return defaults for now
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // we don't store passwords here
    }

    @Override
    public String getUsername() {
        return email; // or userId, whichever you prefer
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
