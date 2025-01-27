package com.github.farmplus.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail (String email); // 이메일로 사용자 찾기
    boolean existsByNickname (String nickname); // 닉네임으로 사용자 찾기
}
