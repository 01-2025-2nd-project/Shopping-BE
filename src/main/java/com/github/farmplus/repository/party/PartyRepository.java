package com.github.farmplus.repository.party;

import com.github.farmplus.repository.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {
    List<Party> findAllByProduct(Product product);
    @Query("SELECT p FROM Party p JOIN FETCH p.partyUserList WHERE p.product = :product")
    List<Party> findAllByProductWithUsers(Product product);
}
