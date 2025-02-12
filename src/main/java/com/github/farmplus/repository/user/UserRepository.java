package com.github.farmplus.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query(" SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailFetchJoin(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    boolean existsByEmail (String email); // 이메일로 사용자 찾기
    boolean existsByNickname (String nickname); // 닉네임으로 사용자 찾기
    @Modifying //변경 필요할때 (UPDATE문)
    @Query("UPDATE User u SET u.money = u.money - :amount WHERE u.userId = :userId AND u.money >= :amount") //쿼리문에 직접 update를 넣어주면 db에서 하나씩 처리 (동시성 고려)
    int deductMoney(@Param("userId") Integer userId, @Param("amount") Double amount);

    @Modifying
    @Query("UPDATE User u SET u.money = u.money + :amount WHERE u.userId = :userId AND (u.money + :amount) >= 0")
    int updateMoney(@Param("userId") Integer userId, @Param("amount") Double amount);



}
