package com.github.farmplus.service.order;

import com.github.farmplus.repository.order.Order;
import com.github.farmplus.repository.order.OrderRepository;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.exceptions.BadRequestException;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.count.TotalCount;
import com.github.farmplus.web.dto.order.request.OrderRequest;
import com.github.farmplus.web.dto.order.response.MyOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository; // 추가

    @Transactional //자동저장 (중간 에러 발생시 원위치)
    @Caching(evict = {
            @CacheEvict(value = "orderList", allEntries = true),
            @CacheEvict(value = "totalOrderCount", key = "#customUserDetails.userId")
    })

    public boolean processOrder(CustomUserDetails customUserDetails, Long productId, OrderRequest orderRequest) {
        // 사용자 정보 가져오기
        User user = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new NotFoundException(customUserDetails.getUserId() + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 상품 정보 가져오기
        Product product = productRepository.findByIdWithLock(productId) // 동시성 비관적 lock으로 해결
                .orElseThrow(() -> new NotFoundException(productId + "에 해당하는 상품을 찾을 수 없습니다."));

        // 상품 수량 확인하기
        if (product.getStock() < orderRequest.getQuantity()) {
            throw new BadRequestException("구매 가능 수량이 부족합니다.");
        }
        product.updateStock(product.getStock() - orderRequest.getQuantity());

        // 주문 생성
        Order order = Order.of(user, product, orderRequest);
        orderRepository.save(order);
        int orderResult = userRepository.deductMoney(user.getUserId(), (double) (orderRequest.getQuantity()*product.getPrice()));
        if (orderResult == 0) {
            throw new BadRequestException("돈이 부족하거나 동시성 문제가 발생하였습니다.");
        }
        return true;
    }

    // 주문 목록 조회 메서드
    @Cacheable(value="orderList", key="#customUserDetails.userId + '_' + pageNum")
    public Page<MyOrder> getOrderList(CustomUserDetails customUserDetails, Integer pageNum) {
        User user = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pageable pageable = PageRequest.of(pageNum, 10); // 한 페이지에 10개씩 표시
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);

        return orders.map(MyOrder::from);
    }


    // 완료된 주문의 총 개수 조회 메서드
    @Cacheable(value = "totalOrderCount", key = "#customUserDetails.userId")
    public TotalCount getTotalOrderCount(CustomUserDetails customUserDetails) {
        User user = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new NotFoundException(customUserDetails.getUserId() + "에 해당하는 사용자를 찾을 수 없습니다."));
        Integer totalCount = orderRepository.findTotalOrders(user);
        return TotalCount.of(totalCount != null ? totalCount : 0);
    }

}
