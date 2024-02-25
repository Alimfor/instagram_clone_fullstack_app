package com.gaziyev.microinstaclone.authservice.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gaziyev.microinstaclone.authservice.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtTokenService tokenProvider;
    private final UserDetailsService userDetailsService;
    private final String serviceUsername;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtConfig.getHeader());

        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!tokenProvider.validateToken(token, false)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
        DecodedJWT jwt = JWT.decode(token);
        String username = jwt.getClaim("username").asString();

        UsernamePasswordAuthenticationToken auth = null;

        if (username.equals(serviceUsername)) {

            List<String> authorities = jwt.getClaim("authorities").asList(String.class);

            auth = new UsernamePasswordAuthenticationToken(username, null,
                    authorities
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );


        } else {
            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(username);

            if (userDetails == null) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            auth = authenticationToken;
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
