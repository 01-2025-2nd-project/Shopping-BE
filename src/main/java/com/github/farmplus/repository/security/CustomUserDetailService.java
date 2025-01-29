package com.github.farmplus.repository.security;

import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailFetchJoin(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with email: " + email));

        // roles가 null이거나 비어 있으면 예외 처리
        List<String> roles = Optional.ofNullable(user.getRoles()) // null 체크
                .filter(rolesList -> !rolesList.isEmpty()) // 비어 있지 않은 경우에만 처리
                .orElseThrow(() -> new UsernameNotFoundException("User has no roles"))
                .stream()
                .map(role -> role.getRoleName())  // Role 객체에서 역할 이름만 추출
                .collect(Collectors.toList());

        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(roles)  // authorities는 List<String>이므로 그대로 사용
                .build();
    }
}
