package com.kosta.userservice.auth.token;


import com.kosta.userservice.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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


}
