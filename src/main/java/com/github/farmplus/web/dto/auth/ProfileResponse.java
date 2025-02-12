package com.github.farmplus.web.dto.auth;

import com.github.farmplus.repository.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;

@Getter
@Builder
public class ProfileResponse {
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String address;
    private Double point;


    public static ProfileResponse from(User user) {
        return ProfileResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .point(user.getMoney())
                .build();
    }

}

