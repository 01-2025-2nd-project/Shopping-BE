package com.github.farmplus.config;

import com.github.farmplus.repository.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 3600000; // 1 hour

    // 토큰 생성 (역할 포함)
    public String generateToken(String email, Set<Role> roles) {
        // ClaimsBuilder 생성 후 .setSubject() 사용 후 build()
        Claims claims = (Claims) Jwts.claims().setSubject(email);

        // 역할 정보를 Claim에 추가
        String roleClaims = roles.stream()
                .map(Role::getRoleName)  // Role 객체에서 roleName을 가져옴
                .collect(Collectors.joining(","));  // 역할을 콤마로 구분하여 하나의 String으로 결합
        claims.put("roles", roleClaims);  // 역할 정보를 'roles' Claim에 저장

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 사용자 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰에서 역할 추출
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String roles = claims.get("roles", String.class);
        return roles == null ? Set.of() : Set.of(roles.split(","));
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
