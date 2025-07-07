package com.kosta.userservice.auth.token;


import com.kosta.userservice.auth.jwt.JwtUtil;
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
