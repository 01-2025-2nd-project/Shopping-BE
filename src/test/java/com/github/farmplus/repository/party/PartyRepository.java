package com.github.farmplus.repository.party;

import com.github.farmplus.repository.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {
    List<Party> findAllByProduct(Product product);
    @Query("SELECT p FROM Party p JOIN FETCH p.partyUserList WHERE p.product = :product")
    List<Party> findAllByProductWithUsers(Product product);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Party p WHERE p.partyId = :partyIid")
    Optional<Party> findByIdWithLock(@Param("id") Long partyId);

}
