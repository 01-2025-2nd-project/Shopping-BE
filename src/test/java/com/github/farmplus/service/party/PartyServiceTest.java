//package com.github.farmplus.service.party;
//
//import com.github.farmplus.repository.category.Category;
//import com.github.farmplus.repository.discount.Discount;
//import com.github.farmplus.repository.discount.DiscountRateRepository;
//import com.github.farmplus.repository.party.Party;
//import com.github.farmplus.repository.party.PartyRepository;
//import com.github.farmplus.repository.party.PartyStatus;
//import com.github.farmplus.repository.product.Product;
//import com.github.farmplus.repository.product.ProductRepository;
//import com.github.farmplus.repository.product_discount.ProductDiscount;
//import com.github.farmplus.repository.product_discount.ProductDiscountRepository;
//import com.github.farmplus.repository.user.User;
//import com.github.farmplus.repository.user.UserRepository;
//import com.github.farmplus.repository.userDetails.CustomUserDetails;
//import com.github.farmplus.web.dto.base.ResponseDto;
//import com.github.farmplus.web.dto.party.request.MakeParty;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//
//public class PartyServiceTest {
//    @Autowired
//    private PartyService partyService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PartyRepository partyRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private DiscountRateRepository discountRepository;
//
//    @Autowired
//    private ProductDiscountRepository productDiscountRepository;
//    private Party party;
//    private List<User> users;
//    @BeforeEach
//    void setUp() {
//        // 상품 생성
//        Product product = Product.builder()
//                .productName("Test Product")
//                .price(1000L)
//                .stock(10L)
//                .build();
//        productRepository.save(product);
//
//        // 할인 생성
//        Discount discount = Discount.builder()
//                .discountRate(0.2)
//                .people(5) // 파티 인원 수
//                .build();
//        discountRepository.save(discount);
//
//        // ProductDiscount 생성
//        ProductDiscount productDiscount = ProductDiscount.builder()
//                .product(product)
//                .discount(discount)
//                .build();
//        productDiscountRepository.save(productDiscount);
//
//        // 파티 생성
//        party = Party.builder()
//                .product(product)
//                .productDiscount(productDiscount)
//                .partyName("Test Party")
//                .endDate(LocalDate.now().plusDays(7))
//                .status(PartyStatus.RECRUITING)
//                .capacity(discount.getPeople())
//                .build();
//        partyRepository.save(party);
//
//        // 테스트용 사용자 생성
//        users = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            User user = User.builder()
//                    .name("User " + i)
//                    .nickname("Nickname " + i)
//                    .email("user" + i + "@test.com")
//                    .password("password")
//                    .money(10000.0)
//                    .build();
//            users.add(userRepository.save(user));
//        }
//    }
//    @Test
//    void testConcurrentPartyJoin() throws InterruptedException {
//        int threadCount = 7; // 동시에 참여 시도할 사용자 수
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        AtomicInteger successCount = new AtomicInteger(0);
//
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executorService.submit(() -> {
//                try {
//                    User user = users.get(index);
//                    CustomUserDetails customUserDetails = new CustomUserDetails(
//                            user.getUserId(),
//                            user.getEmail(),
//                            user.getPassword(),
//                            Collections.singletonList("ROLE_USER") // 적절한 권한 설정
//                    );
//                    ResponseDto response = partyService.joinPartyResult(customUserDetails, party.getPartyId());
//                    if (response.getCode() == HttpStatus.CREATED.value()) {
//                        successCount.incrementAndGet();
//                    }
//                } catch (Exception e) {
//                    System.out.println("User " + index + " failed to join: " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executorService.shutdown();
//
//        assertEquals(party.getCapacity(), successCount.get(), "파티 정원만큼만 참여해야 합니다.");
//
//        Party updatedParty = partyRepository.findById(party.getPartyId()).orElseThrow();
//        assertEquals(PartyStatus.COMPLETED, updatedParty.getStatus(), "파티가 완료 상태여야 합니다.");
//    }
//}
//
