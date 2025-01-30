package com.github.farmplus.repository.product;

import com.github.farmplus.repository.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
//    Page<Product> findAllByCategory(Category category);
//    //전체 일 때 생성일 순
//    Page<Product> findAllByCreateAt(Pageable pageable);
//    //카테고리가 들어갔을 때 생성일 순
//    Page<Product> findAllByCategoryOrderByCreateAtDesc(Category category,Pageable pageable);
//    //전체일 때 구매 많은 순
//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orders o GROUP BY p ORDER BY COUNT(o) DESC")
//    Page<Product> findAllOrderByOrderCountDesc(Pageable pageable);
//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orders o  WHERE p.category = :category GROUP BY p ORDER BY COUNT (o) DESC")
//    Page<Product> findAllByCategoryOrderByOrderCountDesc(Category category,Pageable pageable);
//
//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orders o GROUP BY p ORDER BY COUNT(o) ASC")
//    Page<Product> findAllOrderByOrderCountAsc(Pageable pageable);
//    //카테고리일 때 구맨 많은 순
//
//    //전체일 때 가격순
//    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);
//    Page<Product> findAllByOrderByPriceDesc(Pageable pageable);
//   //카테고리일 때 가격순
//    Page<Product> findAllByCategoryOrderByPriceDesc(Category category,Pageable pageable);
//    Page<Product> findAllByCategoryOrderByPriceAsc(Category category,Pageable pageable);
}
