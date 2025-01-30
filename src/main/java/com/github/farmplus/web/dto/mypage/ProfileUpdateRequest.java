package com.github.farmplus.web.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String nickname;
    private String password;
    private String phoneNumber;
    private String address;
}
