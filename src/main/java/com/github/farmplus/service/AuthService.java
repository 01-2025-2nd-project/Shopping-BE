package com.github.farmplus.service;

import com.github.farmplus.config.security.JwtTokenProvider;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.role.RoleRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userRole.UserRole;
import com.github.farmplus.repository.userRole.UserRoleRepository;
import com.github.farmplus.service.exceptions.CustomExceptions;
import com.github.farmplus.web.dto.auth.Login;
import com.github.farmplus.web.dto.auth.SignUp;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final DataSourceTransactionManager transactionManager;


    @Transactional
    public boolean signUp(SignUp signUpRequest) {

        String name = signUpRequest.getName();
        String nickname = signUpRequest.getNickname();
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String phoneNumber = signUpRequest.getPhoneNumber();
        String address = signUpRequest.getAddress();

        if(userRepository.existsByEmail(email)) {
            throw new CustomExceptions.BadRequestException("이미 존재하는 이메일입니다.");
        }

        User userFound = userRepository.findByName(name)
                .orElseGet(() -> {
                    // 새로 생성된 사용자에 대한 추가 정보를 설정할 수 있습니다.
                    User newUser = userRepository.save(User.builder()
                            .name(name)
                            .email(signUpRequest.getEmail())  // 이메일도 같이 저장
                            .password(passwordEncoder.encode(signUpRequest.getPassword()))  // 비밀번호도 암호화하여 저장
                            .build());
                    return newUser;
                });


        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new CustomExceptions.NotFoundException("ROLE_USER를 찾을 수가 없습니다."));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);
        userRoleRepository.save(UserRole.builder().role(role).user(user).build());
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
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("User를 찾을 수 없습니다."));
            List<String> roles = user.getUserRole();
            return jwtTokenProvider.createToken(email, roles);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomExceptions.NotAcceptException("로그인 할 수 없습니다.");
        }
    }
}
