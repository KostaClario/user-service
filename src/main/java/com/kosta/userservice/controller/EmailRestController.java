package com.kosta.userservice.controller;

import com.kosta.userservice.auth.oauth.CustomOAuth2User;
import com.kosta.userservice.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmailRestController {

    private final EmailService emailService;

    public EmailRestController(EmailService emailService) {
        this.emailService = emailService;
    }

    // 메일 전송
    @PostMapping("send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok().body("인증 코드가 전송되었습니다.");
    }

    // 메일 인증
    @PostMapping("verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean verified = emailService.checkVerificationCode(email, code);
        return verified
                ? ResponseEntity.ok(Collections.singletonMap("verified", true))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("verified", false));
    }

    // 현재 로그인한 사용자 이메일 반환
    @GetMapping("user/email")
    public ResponseEntity<Map<String, String>> getUserEmail(@AuthenticationPrincipal CustomOAuth2User user) {
        String email = user.getEmail();
        Map<String, String> result = new HashMap<>();
        result.put("email", email);
        return ResponseEntity.ok(result);
    }
}
