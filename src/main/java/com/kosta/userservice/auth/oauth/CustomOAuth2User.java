package com.kosta.userservice.auth.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final String picture;

    public CustomOAuth2User(String email, String picture) {
        this.email = email;
        this.picture = picture;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", email, "picture", picture);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}

