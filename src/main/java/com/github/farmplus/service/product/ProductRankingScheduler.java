package com.github.farmplus.service.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.category.CategoryRepository;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product.ProductWithOrderAndParty;
import com.github.farmplus.service.redis.RedisUtil;
import com.github.farmplus.web.dto.product.response.ProductMain;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ProductRankingScheduler {
    private final ProductRepository productRepository;
    private final RedisUtil redisUtil;
    private final CategoryRepository categoryRepository;
    @Scheduled(fixedRate = 3600000)
    @Transactional(readOnly = true)
    public void cacheProductRankings() {

            // 전체 상품 캐싱
            cacheAllProducts("purchaseCount", productRepository.findAllOrderByOrderCountDesc(PageRequest.of(0, 1000)));
            cacheAllProducts("priceAscending", productRepository.findAllByOrderByPriceAsc(PageRequest.of(0, 1000)));
            cacheAllProducts("priceDescending", productRepository.findAllByOrderByPriceDesc(PageRequest.of(0, 1000)));
            cacheAllProducts("createAt", productRepository.findAllByOrderByCreateAtDesc(PageRequest.of(0, 1000)));

            // 카테고리별 캐싱
            List<Category> categories = categoryRepository.findAll();
            for (Category category : categories) {
                cacheCategoryProducts(category, "purchaseCount", productRepository.findAllByCategoryOrderByOrderCountDesc(category, PageRequest.of(0, 1000)));
                cacheCategoryProducts(category, "priceAscending", productRepository.findAllByCategoryOrderByPriceAsc(category, PageRequest.of(0, 1000)));
                cacheCategoryProducts(category, "priceDescending", productRepository.findAllByCategoryOrderByPriceDesc(category, PageRequest.of(0, 1000)));
                cacheCategoryProducts(category, "createAt", productRepository.findAllByCategoryOrderByCreateAtDesc(category, PageRequest.of(0, 1000)));
            }

    }

    private void cacheAllProducts(String sort, Page<ProductWithOrderAndParty> products) {
        List<ProductMain> productList = products.map(ProductMain::of).getContent();
        String redisKey = redisUtil.generateRedisKey("all", sort);
        redisUtil.cacheProductList(redisKey, productList, 2, TimeUnit.HOURS);

    }

    private void cacheCategoryProducts(Category category, String sort, Page<ProductWithOrderAndParty> products) {
        List<ProductMain> productList = products.map(ProductMain::of).getContent();
        String redisKey = redisUtil.generateRedisKey(category.getCategoryName(), sort);
        redisUtil.cacheProductList(redisKey, productList, 2, TimeUnit.HOURS);

    }


}
