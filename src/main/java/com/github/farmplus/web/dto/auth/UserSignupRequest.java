package com.github.farmplus.web.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
}