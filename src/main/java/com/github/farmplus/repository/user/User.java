package com.github.farmplus.repository.user;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.userRole.UserRole;
import com.github.farmplus.web.dto.auth.SignUp;
import com.github.farmplus.web.dto.mypage.ProfileUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@Builder
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 11, nullable = true)
    private String phoneNumber;

    @Column(name = "address", length = 255, nullable = true)
    private String address;
    @Column(name = "money", nullable = false)
    private Double money;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

    public void updateMoney(Double money){
        this.money = money;
    }

    public static User from(SignUp signupRequest) {
        return User.builder()
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .email(signupRequest.getEmail())
                .phoneNumber(signupRequest.getPhoneNumber())
                .address(signupRequest.getAddress())
                .money(0.0)
                .build();
    }

    public void passwordSave(String password) {
        this.password = password;
    }


    public void updateUser(ProfileUpdateRequest profileUpdateRequest) {
        this.nickname = profileUpdateRequest.getNickname();
        this.phoneNumber = profileUpdateRequest.getPhoneNumber();
        this.address = profileUpdateRequest.getAddress();
    }



}
