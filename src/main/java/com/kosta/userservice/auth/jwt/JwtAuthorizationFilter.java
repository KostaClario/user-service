package com.kosta.userservice.auth.jwt;


import com.kosta.userservice.auth.oauth.CustomOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * gateway-service에서 JWT 검증 중이므로 중복 검증될 수 있음
 * 추후 안정화 후 제거 예정
 * */
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(final JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Swagger 및 정적 리소스 화이트리스트
        if (path.startsWith("/swagger") ||
                path.startsWith("/internal") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/webjars") ||
                path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/html") ||
                (path.equals("/api/member") && method.equals("POST"))) {
            log.debug("Bypassing JWT filter for internal path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtUtil.getEmailFromToken(token);
        String picture = jwtUtil.getPictureFromToken(token);
        String provider = jwtUtil.getProviderFromToken(token);
        String providerId = jwtUtil.getProviderIdFromToken(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomOAuth2User user = new CustomOAuth2User(email, picture, provider, providerId);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
    }

