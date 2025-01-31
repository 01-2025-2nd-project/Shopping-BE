package com.github.farmplus.repository.product_discount;

import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount,Integer> {
    List<ProductDiscount> findAllByProduct(Product product);
    @Query("SELECT pd FROM ProductDiscount pd JOIN FETCH pd.discount WHERE pd.product = :product")
    List<ProductDiscount> findAllByProductWithDiscount(Product product);

    Optional<ProductDiscount> findByProductAndDiscount(Product product, Discount discount);
}
