package com.kosta.userservice.controller;

import com.kosta.userservice.auth.jwt.JwtUtil;
import com.kosta.userservice.auth.oauth.CustomOAuth2User;
import com.kosta.userservice.dto.JoinRequestDTO;
import com.kosta.userservice.dto.JoinResponseDTO;
import com.kosta.userservice.dto.ResetPasswordRequestDTO;
import com.kosta.userservice.dto.UpdateProfileRequestDTO;
import com.kosta.userservice.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/member")
    public ResponseEntity<?> me(@RequestHeader("X-User-Email") String email,
                                @RequestHeader("X-User-Picture") String picture) {
        return ResponseEntity.ok("email = " + email + ", picture = " + picture);
    }



    @Operation(
            summary = "회원가입 (비활성 회원 재가입 포함)",
            description = "이메일이 이미 존재하고 비활성화된 경우 재가입 처리합니다.")
    @PostMapping("/member")
    public ResponseEntity<JoinResponseDTO> join(@RequestBody @Validated JoinRequestDTO requestDTO) {

        /**
         * 나중에 bank-service에서 가져올거임 우선 회원가입 성공을 위해 넣어줌
         *         requestDTO.setTotalAmount(0L);
         *         requestDTO.setGoalAmount(0L);
         * */

        memberService.createMember(requestDTO);

        String email = requestDTO.getEmail();
        String picture = requestDTO.getPicture();
        String accessToken = jwtUtil.generateToken(email, picture);

        JoinResponseDTO responseData = new JoinResponseDTO(email, requestDTO.getName(), accessToken);
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
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal CustomOAuth2User user) {
        String email = user.getEmail();
        memberService.removeMember(email);
        return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴 완료");
    }

    @Operation(summary = "비밀번호 재설정",
            description = "현재 로그인된 사용자 비밀번호 재설정",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@AuthenticationPrincipal CustomOAuth2User user,
                                           @RequestBody @Validated ResetPasswordRequestDTO request) {

        memberService.resetPassword(user.getEmail(), request);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 재설정 완료");

    }
}
