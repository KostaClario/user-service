package com.kosta.userservice.auth.oauth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Getter
@EqualsAndHashCode
public class CustomOAuth2User implements OAuth2User {

    private final String email;
    private final String picture;
    private final String provider;
    private final String providerId;

    public CustomOAuth2User(String email, String picture, String provider, String providerId) {
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", email, "picture", picture,
                "provider", provider, "providerId", providerId);
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

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }
}

