package com.github.farmplus.repository.party;

import com.github.farmplus.repository.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {
    List<Party> findAllByProduct(Product product);
    @Query("SELECT p FROM Party p JOIN FETCH p.partyUserList WHERE p.product = :product")
    List<Party> findAllByProductWithUsers(Product product);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Party p WHERE p.partyId = :partyId")
    Optional<Party> findByIdWithLock(@Param("partyId") Long partyId);


    @Query("SELECT COUNT(pa) FROM Party pa")
    Integer findPartyTotalCount();
    @Query("SELECT COUNT(p) FROM Party p JOIN p.partyUserList pu WHERE pu.user.userId = :userId")
    Integer findMyPartyTotalCount(Integer userId);

    List<Party> findAllByEndDateBeforeAndStatus(LocalDate endDate, PartyStatus partyStatus);
    List<Party> findAllByStatus(PartyStatus partyStatus);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Party p SET p.status = :failed WHERE p.endDate < :now AND p.status = :recruiting")
    int updateExpiredParties(@Param("now") LocalDate now,@Param("recruiting") PartyStatus recruiting, @Param("failed") PartyStatus failed);
}
