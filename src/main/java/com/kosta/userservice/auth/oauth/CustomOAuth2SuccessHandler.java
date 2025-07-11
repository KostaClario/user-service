package com.kosta.userservice.auth.oauth;

import com.kosta.userservice.auth.jwt.JwtUtil;
import com.kosta.userservice.auth.token.RefreshTokenService;
import com.kosta.userservice.domain.entity.Member;
import com.kosta.userservice.domain.enums.MemberStatus;
import com.kosta.userservice.domain.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public CustomOAuth2SuccessHandler(MemberRepository memberRepository, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");;

        String accessToken = jwtUtil.generateToken(email, picture);
        String refreshToken = jwtUtil.generateRefreshToken(email, picture);

        refreshTokenService.saveRefreshToken(email, refreshToken);

        log.info("accessToken= {}", accessToken);
        log.info("refreshToken= {}", refreshToken);

        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);


        // 회원 활성화 여부 확인
        Member member = memberRepository.findByEmail(email).orElse(null);

        // 클라이언트에 전달할 redirect URL
        String baseUrl = "http://localhost:8884/html/account/";
        String page = (member != null && MemberStatus.ACTIVE.equals(member.getStatus()))
                ? "auth-success.html"
                : "join.html";

        // accessToken, refreshToken 둘 다 URL로 전달
        String redirectUrl = baseUrl + page +
                "?accessToken=" + encodedAccessToken +
                "&refreshToken=" + encodedRefreshToken;

        log.info("OAuth2 로그인 성공 - 리다이렉트: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}

