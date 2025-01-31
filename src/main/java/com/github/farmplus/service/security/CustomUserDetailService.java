package com.github.farmplus.service.security;

import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.repository.userRole.UserRole;
import com.github.farmplus.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername called for email: {}", email);
        User user = userRepository.findByEmailFetchJoin(email)
                .orElseThrow(()->new NotFoundException(email +"에 해당하는 user는 존재하지 않습니다."));
        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getUserRoles().stream().map(UserRole::getRole).map(Role::getRoleName).collect(Collectors.toList()))
                .build();

    }
}
