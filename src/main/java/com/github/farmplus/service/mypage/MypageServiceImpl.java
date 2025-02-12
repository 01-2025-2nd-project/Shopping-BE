package com.github.farmplus.service.mypage;

import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.service.mypage.MypageService;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse getProfile(CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail())
                .orElseThrow(()-> new NotFoundException(customUserDetails.getEmail() + "에 해당하는 User를 찾을 수 없습니다."));

        ProfileResponse profile = ProfileResponse.from(user);
        return profile;
    }

    @Override
    @Transactional //바꾼값으로 알아서 저장 (변경할때만.. 새로 만드는건 .save 필요)
    public boolean updateProfile(ProfileUpdateRequest updateRequest, CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail())
                .orElseThrow(()-> new NotFoundException(customUserDetails.getEmail() + "에 해당하는 User를 찾을 수 없습니다."));


        user.updateUser(updateRequest);
        String password = passwordEncoder.encode(updateRequest.getPassword());
        user.passwordSave(password);
        return true;
    }

    @Override
    public boolean deleteProfile(CustomUserDetails customUserDetails) {
        // @AuthenticationPrincipal을 통해 인증된 사용자의 정보를 받아옵니다.
        User user = userRepository.findByEmailFetchJoin(customUserDetails.getEmail())
                .orElseThrow(() -> new NotFoundException(customUserDetails.getEmail() + "에 해당하는 User를 찾을 수 없습니다."));
        userRepository.delete(user); // 사용자 삭제
        return true;
    }
}