package com.gaziyev.microinstaclone.authservice.service;

import com.gaziyev.microinstaclone.authservice.configuration.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;


@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtConfig jwtConfig;

	public String generateToken(Authentication authentication) {

		long now = System.currentTimeMillis();

		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim("authorities", authentication.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority).toList()
				)
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + jwtConfig.getExpiration() * 1000L))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public boolean validateToken(String token, UserDetailsService userDetailsService) {
		try {

			final String username = extractUsername(token);
			return (username.equals(
					userDetailsService.loadUserByUsername(username).getUsername()
			) && !isTokenExpired(token));
		} catch (SignatureException e) {
			log.error("Invalid JWT signature.");
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
