package com.github.farmplus.repository.user;

import com.github.farmplus.repository.base.BaseEntity;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.userRole.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;  // 역할 추가

//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
//
//    // 비밀번호를 암호화된 값으로 설정
//    public void setPassword(String password) {
//        this.password = password;
//    }


    public List<String> getUserRole() {
        return (List<String>) this.roles.stream()
                .map(Role::getRoleName)  // Role의 name을 반환
                .collect(Collectors.toSet());
    }


}