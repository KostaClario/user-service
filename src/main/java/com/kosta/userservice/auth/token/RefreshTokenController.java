package com.kosta.userservice.auth.token;


import com.kosta.userservice.auth.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/token")
public class RefreshTokenController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(final JwtUtil jwtUtil, final RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(
            summary = "Access Token 재발급",
            description = "유효한 Refresh Token이 있다면 Access Token을 새로 발급받습니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String userEmail = jwtUtil.getEmailFromToken(refreshToken);
        String picture = jwtUtil.getPictureFromToken(refreshToken);

        if (refreshTokenService.validateRefreshToken(userEmail, refreshToken)) {
            String newAccessToken = jwtUtil.generateToken(userEmail, picture);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지않은 refresh token");
        }
    }


    @Operation(
            summary = "로그아웃",
            description = "Authorization 헤더에 포함된 Access Token에서 사용자 정보를 추출하여 Refresh Token을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {

        log.info("Authorization 헤더 = {}", authHeader);

        if (authHeader != null && !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization 헤더가 유효하지 않습니다.");
        }

        String accessToken = authHeader.replace("Bearer ", "");
        String userEmail = jwtUtil.getEmailFromToken(accessToken);

        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰에서 이메일을 추출할 수 없습니다.");
        }

        refreshTokenService.deleteRefreshToken(userEmail);

        return ResponseEntity.ok("로그아웃 성공: refresh token 삭제");
    }


}
