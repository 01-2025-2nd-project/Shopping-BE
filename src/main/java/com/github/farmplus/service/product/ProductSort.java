package com.github.farmplus.service.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product.ProductWithOrderAndParty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.function.BiFunction;

public enum ProductSort {
    PURCHASE_COUNT("purchaseCount",
            (repo, pageable) -> repo.findAllOrderByOrderCountDesc(pageable),
            (repo, category, pageable) -> repo.findAllByCategoryOrderByOrderCountDesc(category, pageable)),
    PRICE_DESCENDING("priceDescending",
            (repo, pageable) -> repo.findAllByOrderByPriceDesc(pageable),
            (repo, category, pageable) -> repo.findAllByCategoryOrderByPriceDesc(category, pageable)),
    PRICE_ASCENDING("priceAscending",
            (repo, pageable) -> repo.findAllByOrderByPriceAsc(pageable),
            (repo, category, pageable) -> repo.findAllByCategoryOrderByPriceAsc(category, pageable)),
    CREATE_AT("createAt",
            (repo, pageable) -> repo.findAllByOrderByCreateAtDesc(pageable),
            (repo, category, pageable) -> repo.findAllByCategoryOrderByCreateAtDesc(category, pageable));

    private final String sortKey;
    private final BiFunction<ProductRepository, Pageable, Page<ProductWithOrderAndParty>> allProductsFetcher;
    private final TriFunction<ProductRepository, Category, Pageable, Page<ProductWithOrderAndParty>> categoryProductsFetcher;

    ProductSort(String sortKey,
                BiFunction<ProductRepository, Pageable, Page<ProductWithOrderAndParty>> allProductsFetcher,
                TriFunction<ProductRepository, Category, Pageable, Page<ProductWithOrderAndParty>> categoryProductsFetcher) {
        this.sortKey = sortKey;
        this.allProductsFetcher = allProductsFetcher;
        this.categoryProductsFetcher = categoryProductsFetcher;
    }

    public static ProductSort from(String sort) {
        return Arrays.stream(values())
                .filter(s -> s.sortKey.equalsIgnoreCase(sort))
                .findFirst()
                .orElse(CREATE_AT); // 기본값
    }

    public Page<ProductWithOrderAndParty> fetchAllProducts(ProductRepository repository, Pageable pageable) {
        return allProductsFetcher.apply(repository, pageable);
    }

    public Page<ProductWithOrderAndParty> fetchCategoryProducts(ProductRepository repository, Category category, Pageable pageable) {
        return categoryProductsFetcher.apply(repository, category, pageable);
    }
}
