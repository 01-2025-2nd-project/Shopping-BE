package com.github.farmplus.service;

import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.role.RoleRepository;
import com.github.farmplus.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 이메일 중복 확인
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 확인
    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원가입
    public User registerUser(User user) {
        if (isEmailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (isNicknameExists(user.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists");
        }

        // 기본 역할(Role) 가져오기 (예: ROLE_USER)
        Role defaultRole = roleRepository.findById(1) // 기본 역할 ID는 1로 가정
                .orElseThrow(() -> new IllegalStateException("Default role not found"));

        // 비밀번호 암호화 및 역할 추가
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(defaultRole));

        return userRepository.save(user);
    }

    // 로그인
    // UserService.java
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user; // User 객체를 반환
    }

}
