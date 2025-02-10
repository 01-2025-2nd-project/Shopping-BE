package com.github.farmplus.web.controller;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.AuthService;
import com.github.farmplus.web.dto.auth.EmailCheck;
import com.github.farmplus.web.dto.auth.Login;
import com.github.farmplus.web.dto.auth.NicknameCheck;
import com.github.farmplus.web.dto.auth.SignUp;
import com.github.farmplus.web.dto.base.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignController {
    private final AuthService authService;

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

    @PostMapping("/nickname")
    public ResponseDto checkNickname(@RequestBody NicknameCheck nicknameCheck) {
        boolean isExist = authService.isNicknameExist(nicknameCheck.getNickname());

        if (isExist) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "닉네임이 이미 존재합니다.");
        } else {
            return new ResponseDto(HttpStatus.OK.value(), "사용 가능한 닉네임입니다.");
        }
    }

    @PostMapping("/email")
    public ResponseDto checkEmail(@RequestBody EmailCheck emailCheck) {
        boolean isExist = authService.isEmailExist(emailCheck.getEmail());

        if (isExist) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "이메일이 이미 존재합니다.");
        } else {
            return new ResponseDto(HttpStatus.OK.value(), "사용 가능한 이메일입니다.");
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
