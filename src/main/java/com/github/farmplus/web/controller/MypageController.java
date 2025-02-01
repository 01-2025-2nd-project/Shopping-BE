package com.github.farmplus.web.controller;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.mypage.MypageService;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    /**
     * GET /mypage - 프로필 조회
     */
    @GetMapping
    public ResponseDto getProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProfileResponse profile = mypageService.getProfile(customUserDetails);
        if (profile != null) {
            return new ResponseDto(HttpStatus.OK.value(), "프로필 조회 성공", profile);
        } else {
            return new ResponseDto(HttpStatus.NOT_FOUND.value(), "프로필 정보가 없습니다.");
        }
    }

    /**
     * PUT /mypage - 프로필 수정
     */
    @PutMapping
    public ResponseDto updateProfile(@RequestBody ProfileUpdateRequest updateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isUpdated = mypageService.updateProfile(updateRequest, customUserDetails);
        if (isUpdated) {
            return new ResponseDto(HttpStatus.OK.value(), "프로필 수정 성공");
        } else {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "프로필 수정 실패");
        }
    }

    /**
     * DELETE /mypage - 회원 탈퇴
     */
    @DeleteMapping
    public ResponseDto deleteProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boolean isDeleted = mypageService.deleteProfile(customUserDetails);
        if (isDeleted) {
            return new ResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공");
        } else {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "회원 탈퇴 실패");
        }
    }
}
