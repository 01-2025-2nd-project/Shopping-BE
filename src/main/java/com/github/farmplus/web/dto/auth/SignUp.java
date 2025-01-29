package com.github.farmplus.web.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUp {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
}
