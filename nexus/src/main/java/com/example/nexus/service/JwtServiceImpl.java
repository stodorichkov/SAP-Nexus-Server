package com.example.nexus.service;

import com.example.nexus.constant.JwtConstants;
import com.example.nexus.constant.MessageConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(Authentication authentication) {
        final var username = authentication.getName();
        final var roles = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        final var currentDate = new Date();
        final var expireDate = new Date(currentDate.getTime() + JwtConstants.EXPIRATION);

        return Jwts
                .builder()
                .setSubject(username)
                .claim(JwtConstants.ROLES_CLAIM, roles)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(this.getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(this.getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(this.getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(MessageConstants.INVALID_JWT);
        }
    }

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        final var headerAuth = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith(JwtConstants.BEARER)) {
            return headerAuth.replace(JwtConstants.BEARER, "");
        }

        return null;
    }

    private Key getSignKey() {
        final var keyBytes = JwtConstants.SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}