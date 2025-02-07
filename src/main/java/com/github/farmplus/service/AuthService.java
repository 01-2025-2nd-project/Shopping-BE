package com.github.farmplus.service;

import com.github.farmplus.config.security.JwtTokenProvider;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.role.RoleRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userRole.UserRole;
import com.github.farmplus.repository.userRole.UserRoleRepository;

import com.github.farmplus.service.exceptions.BadRequestException;
import com.github.farmplus.service.exceptions.NotAcceptException;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.auth.Login;
import com.github.farmplus.web.dto.auth.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public boolean signUp(SignUp signUpRequest) {
        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String phoneNumber = signUpRequest.getPhoneNumber();
        String address = signUpRequest.getAddress();

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new BadRequestException("이미 존재하는 닉네임입니다.");
        }

        // 새로운 사용자 저장
        User newUser = User.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode(password))  // 비밀번호 암호화
                .phoneNumber(phoneNumber)
                .address(address)
                .money(0.0)
                .build();

        userRepository.save(newUser);

        // 사용자 역할 설정
        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("ROLE_USER를 찾을 수가 없습니다."));

        userRoleRepository.save(UserRole.builder().role(role).user(newUser).build());

        return true;
    }

    public String login(Login loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByEmailFetchJoin(email)
                    .orElseThrow(() -> new NotFoundException("User를 찾을 수 없습니다."));
            List<String> roles = user.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            return jwtTokenProvider.createToken(email, roles);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptException("로그인 할 수 없습니다.");
        }
    }

    public boolean isNicknameExist(String nickname) {
        return userRepository.existsByNickname(nickname);

    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
