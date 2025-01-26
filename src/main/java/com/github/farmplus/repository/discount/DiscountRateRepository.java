package com.github.farmplus.repository.discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRateRepository extends JpaRepository<Discount,Integer> {
}
