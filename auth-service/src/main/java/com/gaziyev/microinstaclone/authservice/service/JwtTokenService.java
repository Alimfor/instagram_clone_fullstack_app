package com.gaziyev.microinstaclone.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.gaziyev.microinstaclone.authservice.configuration.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtConfig jwtConfig;


    public Map<String, String> generateToken(Authentication authentication) throws IllegalArgumentException, JWTCreationException {
        long now = System.currentTimeMillis();

        String accessToken = getToken(
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList(),
                authentication.getName(),
                now, "ACCESS"
        );
        String refreshToken = getToken(
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList(),
                authentication.getName(),
                now, "REFRESH"
        );

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        );
    }

    public Map<String, String> refreshToken(String refreshToken) {

        long now = System.currentTimeMillis();
        DecodedJWT decodedJWT = JWT.decode(refreshToken);
        String username = decodedJWT
                .getClaim("username").asString();
        List<String> authorities = decodedJWT
                .getClaim("authorities").asList(String.class);

        String accessToken = getToken(authorities, username, now, "ACCESS");
        String newRefreshToken = getToken(authorities, username, now, "REFRESH");

        return Map.of(
                "access_token", accessToken,
                "refresh_token", newRefreshToken
        );
    }

    public boolean validateToken(String token) throws JWTVerificationException {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConfig.getSecret()))
                    .withSubject(jwtConfig.getSubject())
                    .withIssuer(jwtConfig.getIssuer())
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            boolean tokenType = jwt.getClaim("isRefreshToken").asBoolean();

            if (!tokenType) {
                log.error("Prohibited from sending access token. Should by refresh token");
                return false;
            }

            return !jwt.toString().isEmpty();
        } catch (SignatureVerificationException e) {
            log.error("Invalid JWT signature.");
        } catch (IncorrectClaimException e) {
            log.error("Invalid JWT claims.");
        } catch (InvalidClaimException e) {
            log.error("JWT claims string is empty.");
        } catch (TokenExpiredException e) {
            log.error("Expired JWT token.");
        } catch (JWTVerificationException e) {
            log.error("Invalid JWT token.");
        }
        return false;
    }

    private String getToken(
            List<String> authoritiesList, String username,
            long now, String tokenType
    ) {
        long expirationMills = now + jwtConfig.getExpiration() * 1000L;
        boolean isRefreshToken = tokenType.equalsIgnoreCase("REFRESH");


        if (isRefreshToken) {
            expirationMills *= 5;
        }

        return JWT.create()
                .withSubject(jwtConfig.getSubject())
                .withClaim("authorities", authoritiesList)
                .withClaim("username", username)
                .withClaim("isRefreshToken", isRefreshToken)
                .withIssuedAt(new Date(now))
                .withIssuer(jwtConfig.getIssuer())
                .withExpiresAt(new Date(expirationMills))
                .sign(Algorithm.HMAC256(jwtConfig.getSecret()));
    }
}
