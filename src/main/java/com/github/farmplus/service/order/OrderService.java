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
import com.github.farmplus.web.dto.count.TotalCount;
import com.github.farmplus.web.dto.order.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository; // 추가

    public boolean processOrder(CustomUserDetails customUserDetails, Long productId, OrderRequest orderRequest) {
        // 사용자 정보 가져오기
        User user = userRepository.findById(customUserDetails.getUserId()).orElse(null);
        if (user == null) {
            return false; // 사용자 정보가 없으면 처리 실패
        }

        // 상품 정보 가져오기
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }

        // 사용 가능한 파티 찾기
        Party party = findAvailableParty(product);
        if (party == null) {
            return false;
        }

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .product(product)
                .party(party)
                .quantity(orderRequest.getQuantity())
                .price(product.getPrice() * orderRequest.getQuantity())
                .finalPrice(orderRequest.getFinalPrice())
                .build();

        orderRepository.save(order);
        return true;
    }

    // 주문 목록 조회 메서드
    public List<Order> getOrderList(Integer userId, Integer pageNum) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return List.of(); // 사용자 정보가 없으면 빈 리스트 반환
        }
        return orderRepository.findAllByUser(user); // 페이지네이션 로직 추가 필요
    }

    // 완료된 주문의 총 개수 조회 메서드
    public TotalCount getTotalOrderCount(CustomUserDetails customUserDetails) {
        User user = userRepository.findById(customUserDetails.getUserId()).orElse(null);
        Integer totalCount = orderRepository.findTotalOrders(user);
        return TotalCount.of(totalCount != null ? totalCount : 0);
    }

    // 사용 가능한 파티를 찾는 로직 (유지)
    private Party findAvailableParty(Product product) {
        List<Party> parties = partyRepository.findAllByProduct(product);
        return parties.isEmpty() ? null : parties.get(0);
    }
}
