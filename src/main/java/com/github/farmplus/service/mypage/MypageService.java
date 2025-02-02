package com.github.farmplus.service.mypage;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;

public interface MypageService {
    ProfileResponse getProfile(CustomUserDetails customUserDetails);
    boolean updateProfile(ProfileUpdateRequest updateRequest, CustomUserDetails customUserDetails);
    boolean deleteProfile(CustomUserDetails customUserDetails);
}