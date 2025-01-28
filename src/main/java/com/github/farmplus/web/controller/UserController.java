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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UserSignupRequest request){
        // 이메일 중복 체크
        if (userService.isEmailExists(request.getEmail())) {
            return new ResponseEntity<>(new UserApiResponse(409, request.getEmail() + "는 이미 존재하는 이메일입니다. 다른 이메일을 이용해주세요.", true), HttpStatus.CONFLICT);
        }

        // 닉네임 중복 체크
        if (userService.isNicknameExists(request.getNickname())) {
            return new ResponseEntity<>(new UserApiResponse(409, "는 이미 존재하는 닉네임입니다. 다른 닉네임을 이용해주세요.", true), HttpStatus.CONFLICT);
        }

        // 사용자 등록
        User newUser = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(request.getPassword()) // 암호화 필요
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        userService.registerUser(newUser);

        return new ResponseEntity<>(new UserApiResponse(200, "user" + request.getNickname()+ "님 회원 가입에 성공하셨습니다."), HttpStatus.OK);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());

        if(user == null) {
            return new ResponseEntity<>("Cannot find user with ID", HttpStatus.NOT_FOUND);
        }

        //JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getEmail());

        if(token == null) {
            return new ResponseEntity<>("Login not possible", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 헤더에 토큰 포함
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", "Bearer" + token);

        return new ResponseEntity<>(new UserApiResponse(200, "Login Success"), responseHeaders, HttpStatus.OK);
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        if (userService.isNicknameExists(nickname)){
            return new ResponseEntity<>(new UserApiResponse(409, nickname + "는 이미 존재하는 닉네임입니다. 다른 닉네임을 이용해주세요.", true), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new UserApiResponse(200, nickname + "사용하실 수 있는 닉네임입니다."), HttpStatus.OK);
    }

    // 이메일 중복 확인
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        if (userService.isEmailExists(email)) {
            return new ResponseEntity<>(new UserApiResponse(409, email + "는 이미 존재하는 이메일입니다. 다른 이메일을 이용해주세요.", true), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new UserApiResponse(200, email + "는 사용하실 수 있는 이메일입니다."), HttpStatus.OK);
    }
}
