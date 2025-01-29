package com.github.farmplus.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // HS256 알고리즘에 적합한 키 생성
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private long tokenValidMilisecond = 1000L * 60 * 60; // 1시간

    private final UserDetailsService userDetailsService;

    // "X-AUTH-TOKEN" 대신 "Authorization" 헤더에서 JWT를 추출
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // "Bearer " 접두사를 제거한 토큰 반환
        }
        return null;
    }

    public String createToken(String email, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검증 메소드에서 예외를 더 구체적으로 처리
    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken).getBody();
            Date now = new Date();
            return !claims.getExpiration().before(now);
        } catch (Exception e) {
            return false; // 예외가 발생하면 토큰이 유효하지 않다고 간주
        }
    }

    // 토큰을 이용해 인증 객체 반환
    public Authentication getAuthentication(String jwtToken) {
        String email = getUserEmail(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserEmail(String jwtToken) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken).getBody().getSubject();
    }
}
