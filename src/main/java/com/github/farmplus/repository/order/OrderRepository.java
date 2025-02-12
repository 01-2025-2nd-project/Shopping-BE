package com.github.farmplus.repository.order;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findAllByParty(Party party);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Integer findTotalOrders(User user);
    List<Order> findAllByUser(User user);
    Page<Order> findAllByUser(User user, Pageable pageable);
}
