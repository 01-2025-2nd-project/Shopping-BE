package com.github.farmplus.service.mypage;

import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import com.github.farmplus.web.dto.auth.ProfileResponse;

    public interface MypageService {
        ProfileResponse getProfile();
        boolean updateProfile(ProfileUpdateRequest updateRequest);
        boolean deleteProfile();
    }

