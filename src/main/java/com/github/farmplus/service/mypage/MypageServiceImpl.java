package com.github.farmplus.service.mypage;

import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.mypage.MypageService;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;

    @Override
    public ProfileResponse getProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail()).orElse(null);
        if (user == null) {
            return null;
        }
        ProfileResponse profile = new ProfileResponse();
        profile.setName(user.getName());
        profile.setNickname(user.getNickname());
        profile.setEmail(user.getEmail());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setAddress(user.getAddress());
        profile.setPoint(0); // 포인트 계산 로직 추가 필요
        return profile;
    }

    @Override
    public boolean updateProfile(ProfileUpdateRequest updateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail()).orElse(null);
        if (user == null) {
            return false;
        }
        // 요청받은 정보로 사용자 정보 수정
        user.setNickname(updateRequest.getNickname());
        user.setPassword(updateRequest.getPassword());  // 비밀번호 수정
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setAddress(updateRequest.getAddress());
        userRepository.save(user); // 변경된 정보 저장
        return true;
    }

    @Override
    public boolean deleteProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail()).orElse(null);
        if (user == null) {
            return false;
        }
        userRepository.delete(user); // 사용자 삭제
        return true;
    }
}
