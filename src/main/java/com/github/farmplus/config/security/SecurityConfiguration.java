package com.github.farmplus.config.security;

import com.github.farmplus.web.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider; // JwtAuthenticationFilter 생성에 필요



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers((h)->h.frameOptions((f)->f.sameOrigin()))
                .csrf((c)->c.disable())
                .httpBasic(hb->hb.disable())
                .formLogin((fl)->fl.disable())
                .rememberMe((rm)->rm.disable())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(c-> c.configurationSource(corsConfig()))
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().permitAll()  // 모든 요청에 대해 인증 없이 접근 허용
                )
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(new CustomerAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomerAccessDeniedHandler()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private CorsConfigurationSource corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setAllowedOrigins(List.of(
                "https://frontend-ecru-phi-22.vercel.app",  // 배포된 프론트엔드 URL
                "http://localhost:3000",                    // 로컬 환경에서 실행되는 프론트엔드 URL (예: React 앱)
                "http://localhost:8080",
                "https://*.ngrok.io",                       // ngrok URL 패턴 (동적 URL 허용)
                "https://jiangxy.github.io" ));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addExposedHeader("Authorization"); //추가
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-refresh", "token"));
        corsConfiguration.setAllowedMethods(List.of("GET","PUT","POST","DELETE","OPTIONS"));
        corsConfiguration.setMaxAge(1000L*60*60);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
