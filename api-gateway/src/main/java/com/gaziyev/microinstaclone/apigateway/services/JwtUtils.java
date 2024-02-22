package com.gaziyev.microinstaclone.apigateway.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtils {


    public DecodedJWT getDecodedJWT(String token) {
        return JWT.decode(token);
    }

    public boolean isExpired(String token) {
        return getDecodedJWT(token).getExpiresAt()
                .before(new java.util.Date());
    }
}
