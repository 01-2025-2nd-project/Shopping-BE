package com.github.farmplus.service;

import com.github.farmplus.config.security.JwtTokenProvider;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.web.dto.auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    public String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "YOUR_KAKAO_REST_API_KEY");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (String) response.getBody().get("access_token");
        } else {
            throw new RuntimeException("카카오 Access Token 발급 실패");
        }
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
            String email = (String) kakaoAccount.get("email");
            String nickname = (String) ((Map<String, Object>) response.getBody().get("properties")).get("nickname");

            return new KakaoUserInfo(email, nickname);
        } else {
            throw new RuntimeException("카카오 사용자 정보 요청 실패");
        }
    }

    public String processKakaoLogin(KakaoUserInfo kakaoUserInfo) {
        // 사용자 정보 DB 확인
        User user = userRepository.findByEmailFetchJoin(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    // 기존 사용자가 없으면 회원가입
                    User newUser = User.builder()
                            .email(kakaoUserInfo.getEmail())
                            .nickname(kakaoUserInfo.getNickname())
                            .password(passwordEncoder.encode("KAKAO_SOCIAL")) // 랜덤 패스워드 설정
                            .build();
                    return userRepository.save(newUser);
                });

        // JWT 발급
        return jwtTokenProvider.createToken(user.getEmail(), List.of("ROLE_USER"));
    }

}
