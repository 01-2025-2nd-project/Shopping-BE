package com.github.farmplus.web.filters;

import com.github.farmplus.config.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = jwtTokenProvider.resolveToken(request);
            if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
                Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("JWT 인증 성공: " + auth.getName());
            } else {
                log.warn("유효하지 않은 JWT 또는 토큰 없음");
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT 만료됨: ", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT가 만료되었습니다.");
            return;
        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생: ", e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JWT 인증 실패");
            return;
        }

        filterChain.doFilter(request, response);
    }
}