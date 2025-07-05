package com.kosta.userservice.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(String email, String picture){
        return JWT.create()
                .withSubject("User")
                .withClaim("email", email)
                .withClaim("picture", picture)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public String generateRefreshToken(String email, String picture){
        return JWT.create()
                .withSubject("RefreshToken")
                .withClaim("email", email)
                .withClaim("picture", picture)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public String getEmailFromToken(String token){
        try{
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                    .build()
                    .verify(token);
            return jwt.getClaim("email").asString();
        }catch (JWTVerificationException e) {
            return null;
        }
    }

    public String getPictureFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                    .build()
                    .verify(token);
            return jwt.getClaim("picture").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean validateToken(String token){
        try {
            JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

}
