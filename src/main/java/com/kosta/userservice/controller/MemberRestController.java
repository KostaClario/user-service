package com.kosta.userservice.controller;

import com.kosta.userservice.auth.jwt.JwtUtil;
import com.kosta.userservice.auth.oauth.CustomOAuth2User;
import com.kosta.userservice.domain.entity.Member;
import com.kosta.userservice.dto.*;
import com.kosta.userservice.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberRestController {

    private final MemberServiceImpl memberService;
    private final JwtUtil jwtUtil;

    public MemberRestController(MemberServiceImpl memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }


    @Operation(
            summary = "헤더에 유저정보 가져오기",
            description = "헤더에 이메일, 이름, 프로필사진 가져옴")
    @GetMapping("/member")
    public ResponseEntity<MemberInfoResponse> getUserInfo(@AuthenticationPrincipal CustomOAuth2User user) {

        Member member = memberService.getMemberByEmail(user.getEmail());
        MemberInfoResponse response = new MemberInfoResponse();
        response.setEmail(member.getEmail());
        response.setName(member.getName());
        response.setPicture(user.getPicture());

        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "회원가입 (비활성 회원 재가입 포함)",
            description = "이메일이 이미 존재하고 비활성화된 경우 재가입 처리합니다.")
    @PostMapping("/member")
    public ResponseEntity<JoinResponseDTO> join(@RequestBody @Validated JoinRequestDTO requestDTO,
                                                HttpServletRequest httpRequest) {

        /**
         * 나중에 bank-service에서 가져올거임 우선 회원가입 성공을 위해 넣어줌
         *         requestDTO.setTotalAmount(0L);
         *         requestDTO.setGoalAmount(0L);
         * */

        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            requestDTO.setProvider(jwtUtil.getProviderFromToken(token));
            requestDTO.setProviderId(jwtUtil.getProviderIdFromToken(token));
            requestDTO.setEmail(jwtUtil.getEmailFromToken(token));
        }

        // 회원 생성
        memberService.createMember(requestDTO);

        // accessToken 재발급
        String accessToken = jwtUtil.generateToken(
                requestDTO.getEmail(),
                requestDTO.getPicture(),
                requestDTO.getProvider(),
                requestDTO.getProviderId()
        );

        JoinResponseDTO responseData = new JoinResponseDTO(
                requestDTO.getEmail(),
                requestDTO.getName(),
                accessToken
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }


    @Operation(
            summary = "회원 정보 수정",
            description = "이름과 전화번호를 수정",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/member")
    public ResponseEntity<String> updateProfile(
            @RequestBody @Validated UpdateProfileRequestDTO requestDTO,
            @AuthenticationPrincipal CustomOAuth2User user) {

        String email = user.getEmail();
        memberService.updateProfile(email, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보 수정 성공");
    }



    @Operation(summary = "회원 탈퇴", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/member")
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal CustomOAuth2User user,
                                          @RequestBody @Validated RemoveMemberRequestDTO request) {

        boolean success = memberService.removeMember(user.getEmail(), request.getPassword());

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴 완료");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
    }

    @Operation(summary = "비밀번호 재설정",
            description = "현재 로그인된 사용자 비밀번호 재설정",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/member/reset-password")
    public ResponseEntity<?> resetPassword(@AuthenticationPrincipal CustomOAuth2User user,
                                           @RequestBody @Validated ResetPasswordRequestDTO request) {

        memberService.resetPassword(user.getEmail(), request);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 재설정 완료");

    }

    @Operation(summary = "비밀번호 체크",
            description = "비밀번호 체크 후 개인정보수정 페이지 입장",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/member/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody @Validated PasswordCheckRequestDTO requestDTO,
                                           @AuthenticationPrincipal CustomOAuth2User user) {

        boolean result = memberService.checkPassword(user.getEmail(), requestDTO);

        if (result) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호가 일치하지 않습니다.");
        }

    }

    @GetMapping("/member/email")
    public ResponseEntity<Map<String, String>> getEmail(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> result = new HashMap<>();
        result.put("email", user.getEmail());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/member/id")
    public ResponseEntity<MemberIdResponseDTO> getMemberId(@AuthenticationPrincipal CustomOAuth2User user) {
        Member member = memberService.getMemberByEmail(user.getEmail());
        return ResponseEntity.ok(new MemberIdResponseDTO(member.getMemberId()));
    }
}
