package com.github.farmplus.repository.user;

import com.github.farmplus.repository.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 11, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    // 비밀번호를 암호화된 값으로 설정
    public void setPassword(String password) {
        this.password = password;
    }
}