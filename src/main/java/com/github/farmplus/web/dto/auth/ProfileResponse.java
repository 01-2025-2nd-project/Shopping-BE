package com.github.farmplus.web.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String address;
    private int point;
}

