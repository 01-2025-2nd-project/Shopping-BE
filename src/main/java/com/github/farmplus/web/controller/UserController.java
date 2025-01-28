package com.github.farmplus.web.controller;

import com.github.farmplus.config.JwtTokenProvider;
import com.github.farmplus.repository.user.*;
import com.github.farmplus.service.UserService;
import com.github.farmplus.web.dto.auth.UserApiResponse;
import com.github.farmplus.web.dto.auth.UserLoginRequest;
import com.github.farmplus.web.dto.auth.UserSignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserSignupRequest request) {
        if (userService.isEmailExists(request.getEmail())) {
            return new ResponseEntity<>(new UserApiResponse(409, request.getEmail() + "는 이미 존재하는 이메일입니다. 다른 이메일을 이용해주세요.", true), HttpStatus.CONFLICT);
        }

        if (userService.isNicknameExists(request.getNickname())) {
            return new ResponseEntity<>(new UserApiResponse(409, request.getNickname() + "는 이미 존재하는 닉네임입니다. 다른 닉네임을 이용해주세요.", true), HttpStatus.CONFLICT);
        }

        // 유저 생성
        User newUser = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())  // 닉네임 처리
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // 비밀번호 암호화
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        userService.registerUser(newUser);  // 사용자 등록 서비스 호출

        return new ResponseEntity<>(new UserApiResponse(200, "user " + request.getNickname() + "님 회원 가입에 성공하셨습니다."), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            // 사용자 검증
            User user = userService.login(request.getEmail(), request.getPassword());

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRoles());  // 역할 전달

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", "Bearer " + token);

            return new ResponseEntity<>(new UserApiResponse(200, "Login Success"), responseHeaders, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new UserApiResponse(401, e.getMessage(), true), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        if (userService.isNicknameExists(nickname)) {
            return new ResponseEntity<>(new UserApiResponse(409, nickname + "는 이미 존재하는 닉네임입니다. 다른 닉네임을 이용해주세요.", true), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new UserApiResponse(200, nickname + "는 사용하실 수 있는 닉네임입니다."), HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        if (userService.isEmailExists(email)) {
            return new ResponseEntity<>(new UserApiResponse(409, email + "는 이미 존재하는 이메일입니다. 다른 이메일을 이용해주세요.", true), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new UserApiResponse(200, email + "는 사용하실 수 있는 이메일입니다."), HttpStatus.OK);
    }
}
