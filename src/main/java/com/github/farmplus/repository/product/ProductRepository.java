package com.github.farmplus.repository.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductName(String productName);
//    Page<Product> findAllByCategory(Category category);
    //전체 일 때 생성일 순
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(DISTINCT o), COUNT(DISTINCT pa)) " +
        "FROM Product p " +
        "LEFT JOIN p.orders o " +
        "LEFT JOIN p.parties pa " +
        "GROUP BY p " +
        "ORDER BY p.createAt DESC")
    Page<ProductWithOrderAndParty> findAllByOrderByCreateAtDesc(Pageable pageable);
    //카테고리가 들어갔을 때 생성일 순
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(DISTINCT o), COUNT(DISTINCT pa)) " +
        "FROM Product p " +
        "LEFT JOIN p.orders o "+
        "LEFT JOIN p.parties pa "+
        "WHERE p.category = :category " +
        "GROUP BY p " +
        "ORDER BY p.createAt")
    Page<ProductWithOrderAndParty> findAllByCategoryOrderByCreateAtDesc(Category category,Pageable pageable);
    //전체일 때 구매 많은 순
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(DISTINCT o), COUNT(DISTINCT pa)) " +
            "FROM Product p " +
            "LEFT JOIN p.orders o " +
            "LEFT JOIN p.parties pa " +
            "GROUP BY p ORDER BY COUNT(o) DESC")
    Page<ProductWithOrderAndParty> findAllOrderByOrderCountDesc(Pageable pageable);
//    //카테고리일 때 구맨 많은 순

//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orders o  WHERE p.category = :category GROUP BY p ORDER BY COUNT (o) DESC")
//    Page<Product> findAllByCategoryOrderByOrderCountDesc(Category category,Pageable pageable);
@Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(DISTINCT o), COUNT(DISTINCT pa)) " +
        "FROM Product p " +
        "LEFT JOIN p.orders o " +
        "LEFT JOIN p.parties pa " +
        "WHERE p.category = :category " +
        "GROUP BY p " +
        "ORDER BY COUNT(o) DESC")
    Page<ProductWithOrderAndParty> findAllByCategoryOrderByOrderCountDesc(Category category, Pageable pageable);



//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orders o GROUP BY p ORDER BY COUNT(o) ASC")
//    Page<Product> findAllOrderByOrderCountAsc(Pageable pageable);

//
//    //전체일 때 가격순
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(o), COUNT(pa)) " +
        "FROM Product p " +
        "LEFT JOIN p.orders o " +
        "LEFT JOIN p.parties pa " +
        "GROUP BY p " +
        "ORDER BY p.price ASC")
    Page<ProductWithOrderAndParty> findAllByOrderByPriceAsc(Pageable pageable);
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(o), COUNT(pa)) " +
            "FROM Product p " +
            "LEFT JOIN p.orders o " +
            "LEFT JOIN p.parties pa " +
            "GROUP BY p " +
            "ORDER BY p.price DESC")
    Page<ProductWithOrderAndParty> findAllByOrderByPriceDesc(Pageable pageable);
//   //카테고리일 때 가격순
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(o), COUNT(pa)) " +
        "FROM Product p " +
        "LEFT JOIN p.orders o " +
        "LEFT JOIN p.parties pa " +
        "WHERE p.category = :category " +
        "GROUP BY p " +
        "ORDER BY p.price DESC")
    Page<ProductWithOrderAndParty> findAllByCategoryOrderByPriceDesc(Category category,Pageable pageable);
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(o), COUNT(pa)) " +
            "FROM Product p " +
            "LEFT JOIN p.orders o " +
            "LEFT JOIN p.parties pa " +
            "WHERE p.category = :category " +
            "GROUP BY p " +
            "ORDER BY p.price ASC")
    Page<ProductWithOrderAndParty> findAllByCategoryOrderByPriceAsc(Category category,Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM Product p")
    Integer findProductTotalCount();
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryName =:category")
    Integer findProductByCategoryTotalCount(String category);
    @Query("SELECT new com.github.farmplus.repository.product.ProductWithOrderAndParty(p, COUNT(o), COUNT(pa)) " +
            "FROM Product p " +
            "LEFT JOIN p.orders o " +
            "LEFT JOIN p.parties pa " +
            "WHERE p.productName LIKE %:keyword% "+
            "GROUP BY p")
    Page<ProductWithOrderAndParty> findSearchProduct(@Param("keyword")String keyword, Pageable pageable);
}
