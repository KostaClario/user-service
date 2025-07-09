package com.kosta.userservice.service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증 정보 (코드 + 만료시간) 저장
    private final Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();

    // 내부 클래스로 코드와 만료시간 관리
    private static class VerificationInfo {
        String code;
        long expiryTime;

        VerificationInfo(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 인증코드 생성 및 이메일 발송
    public void sendVerificationCode(String email) {
        String code = generateRandomCode();
        long expiry = System.currentTimeMillis() + (5 * 60 * 1000); // 5분 후 만료

        verificationCodes.put(email, new VerificationInfo(code, expiry));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Clario] 이메일 인증 코드");
        message.setText("인증코드: " + code + "\n(5분 이내에 입력해주세요)");

        mailSender.send(message);
    }

    // 6자리 난수 생성
    public String generateRandomCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    // 인증코드 확인 + 유효시간 체크
    public boolean checkVerificationCode(String email, String code) {
        VerificationInfo info = verificationCodes.get(email);
        if (info == null) return false;

        boolean isExpired = System.currentTimeMillis() > info.expiryTime;
        return !isExpired && code.equals(info.code);
    }
}
