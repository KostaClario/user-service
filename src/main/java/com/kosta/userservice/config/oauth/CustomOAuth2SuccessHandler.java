package com.kosta.userservice.config.oauth;

import com.kosta.userservice.config.jwt.JwtUtil;
import com.kosta.userservice.domain.Member;
import com.kosta.userservice.domain.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public CustomOAuth2SuccessHandler(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");;

        String token = jwtUtil.generateToken(email, picture);

        // 회원 활성화 여부 확인
        Member member = memberRepository.findByEmail(email).orElse(null);

        String redirectUrl;
        if (member != null && Boolean.TRUE.equals(member.isActivated())) {
            redirectUrl = "http://localhost:8884/html/account/auth-success.html?token=" + token;
        } else {
            redirectUrl = "http://localhost:8884/html/account/privacy.html?token=" + token;
        }

        log.info("OAuth2 로그인 성공 - 리다이렉트: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}

