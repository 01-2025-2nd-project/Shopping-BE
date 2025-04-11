package com.github.farmplus;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.category.CategoryRepository;
import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.discount.DiscountRateRepository;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.party.PartyStatus;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import com.github.farmplus.repository.product_discount.ProductDiscountRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.service.exceptions.StockShortageException;
import com.github.farmplus.service.party.PartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PartyServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private DiscountRateRepository discountRepository;

    @Autowired
    private PartyService partyService;

    private Product product;
    private Discount discount;
    private Category category;
    private ProductDiscount productDiscount;

    @BeforeEach
    void setUp() {
        category = categoryRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("카테고리 찾을 수 없음"));

        product = Product.builder()
                .productName("Test Product")
                .price(1000L)
                .stock(3L)
                .category(category)
                .build();
        product = productRepository.save(product);

        discount = Discount.builder()
                .people(3)
                .discountRate(0.1)
                .build();
        discount = discountRepository.save(discount);

        productDiscount = ProductDiscount.builder()
                .product(product)
                .discount(discount)
                .build();
        productDiscount = productDiscountRepository.save(productDiscount);
    }

    @Test
    void testConcurrentPartyJoin() throws InterruptedException {
        // Given: 3개의 파티 생성
        Party party1 = Party.builder()
                .product(product)
                .productDiscount(productDiscount)
                .partyName("파티1")
                .endDate(LocalDate.now().plusDays(1))
                .status(PartyStatus.RECRUITING)
                .capacity(1)
                .build();
        Party party2 = Party.builder()
                .product(product)
                .productDiscount(productDiscount)
                .partyName("파티2")
                .endDate(LocalDate.now().plusDays(1))
                .status(PartyStatus.RECRUITING)
                .capacity(1)
                .build();
        Party party3 = Party.builder()
                .product(product)
                .productDiscount(productDiscount)
                .partyName("파티3")
                .endDate(LocalDate.now().plusDays(1))
                .status(PartyStatus.RECRUITING)
                .capacity(1)
                .build();

        final Party savedParty1 = partyRepository.save(party1);
        final Party savedParty2 = partyRepository.save(party2);
        final Party savedParty3 = partyRepository.save(party3);

        // 테스트용 더미 유저
        User user1 = User.builder().userId(1).money(10000.0).build();
        User user2 = User.builder().userId(2).money(10000.0).build();
        User user3 = User.builder().userId(3).money(10000.0).build();

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable task1 = () -> {
            try {
                System.out.println("Task1: 참여 파티 " + savedParty1.getPartyId());
                partyService.joinPartyResult(user1, savedParty1.getPartyId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Task1 실패: " + e.getMessage());
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };
        Runnable task2 = () -> {
            try {
                System.out.println("Task2: 참여 파티 " + savedParty2.getPartyId());
                partyService.joinPartyResult(user2, savedParty2.getPartyId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Task2 실패: " + e.getMessage());
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };
        Runnable task3 = () -> {
            try {
                System.out.println("Task3: 참여 파티 " + savedParty3.getPartyId());
                partyService.joinPartyResult(user3, savedParty3.getPartyId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Task3 실패: " + e.getMessage());
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        executorService.submit(task1);
        executorService.submit(task2);
        executorService.submit(task3);

        latch.await();
        executorService.shutdown();

        // Then: 검증
        Product updatedProduct = productRepository.findByIdWithLock(product.getProductId()).get();
        System.out.println("Final stock: " + updatedProduct.getStock());
        System.out.println("Success count: " + successCount.get() + ", Fail count: " + failCount.get());
        assertEquals(0, updatedProduct.getStock());
        assertEquals(1, successCount.get());
        assertEquals(2, failCount.get());
    }
}