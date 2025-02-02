package com.github.farmplus.web.controller;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.AuthService;
import com.github.farmplus.service.KakaoAuthService;
import com.github.farmplus.web.dto.auth.KakaoUserInfo;
import com.github.farmplus.web.dto.auth.Login;
import com.github.farmplus.web.dto.auth.SignUp;
import com.github.farmplus.web.dto.base.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignController {
    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;

    @PostMapping(value = "/sign-up")
    public ResponseDto register(@RequestBody SignUp signUpRequest){
        boolean isSuccess = authService.signUp(signUpRequest);
        if (isSuccess) {
            return new ResponseDto(HttpStatus.OK.value(), "회원가입에 성공하였습니다.");
        } else {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "회원가입에 실패하였습니다.");
        }
    }

    @PostMapping(value = "/login")
    public ResponseDto login(@RequestBody Login loginRequest, HttpServletResponse httpServletResponse) {
        String token = authService.login(loginRequest);
        if (token != null && !token.isEmpty()) {
            httpServletResponse.setHeader("Authorization", "Bearer " + token);
            return new ResponseDto(HttpStatus.OK.value(), "로그인에 성공하였습니다.", token);
        } else {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "로그인에 실패하였습니다.");
        }
    }

    @GetMapping("/kakao-login")
    public ResponseEntity<ResponseDto> kakaoLoginUrl() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=YOUR_KAKAO_REST_API_KEY" +
                "&redirect_uri=http://localhost:8080/auth/kakao/callback";

        return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "카카오 로그인 URL 생성 완료", kakaoLoginUrl));
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseDto> kakaoCallback(@RequestParam("code") String code) {
        try {
            // Access Token 가져오기
            String accessToken = kakaoAuthService.getAccessToken(code);  // 수정된 부분

            // 카카오 사용자 정보 가져오기
            KakaoUserInfo kakaoUserInfo = kakaoAuthService.getUserInfo(accessToken);  // 수정된 부분

            // 사용자 로그인 처리
            String jwtToken = kakaoAuthService.processKakaoLogin(kakaoUserInfo);  // 수정된 부분

            return ResponseEntity.ok(new ResponseDto(HttpStatus.OK.value(), "카카오 로그인 성공", jwtToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "카카오 로그인 실패"));
        }
    }



    @GetMapping("/test")
    public ResponseDto test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "인증 정보가 없습니다.");
        }
        return new ResponseDto(HttpStatus.OK.value(), "요청이 성공했습니다.", customUserDetails.getEmail());
    }
}
