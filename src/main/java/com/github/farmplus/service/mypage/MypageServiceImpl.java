package com.github.farmplus.service.mypage;

import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.service.mypage.MypageService;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;

    @Override
    public ProfileResponse getProfile() {
        // 현재 로그인된 사용자를 가져오는 로직 (예: SecurityContextHolder)
        User user = getCurrentUser();
        if (user == null) {
            return null;
        }
        ProfileResponse profile = new ProfileResponse();
        profile.setName(user.getName());
        profile.setNickname(user.getNickname());
        profile.setEmail(user.getEmail());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setAddress(user.getAddress());
        profile.setPoint(0); // 포인트는 User 엔티티에 없으므로 계산 로직 추가 필요
        return profile;
    }

    @Override
    public boolean updateProfile(ProfileUpdateRequest updateRequest) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        // 업데이트 데이터 적용
        user.setNickname(updateRequest.getNickname());
        user.setPassword(updateRequest.getPassword());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setAddress(updateRequest.getAddress());
        userRepository.save(user); // 변경 사항 저장
        return true;
    }

    @Override
    public boolean deleteProfile() {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        userRepository.delete(user); // 사용자 삭제
        return true;
    }

    private User getCurrentUser() {
        // 현재 로그인된 사용자 가져오기 (예: SecurityContextHolder)
        return userRepository.findById(1).orElse(null); // 예제에서는 임시로 ID=1 사용
    }
}
