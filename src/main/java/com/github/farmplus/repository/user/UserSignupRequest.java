package com.github.farmplus.repository.user;

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