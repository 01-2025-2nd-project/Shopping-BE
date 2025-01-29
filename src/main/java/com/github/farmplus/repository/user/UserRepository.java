package com.github.farmplus.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query(" SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailFetchJoin(String email);
    Optional<User> findByName(String name);
    boolean existsByEmail (String email); // 이메일로 사용자 찾기
    boolean existsByNickname (String nickname); // 닉네임으로 사용자 찾기

}
