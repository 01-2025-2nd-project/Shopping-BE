package com.github.farmplus.web.controller;

import com.github.farmplus.repository.order.OrderRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.order.OrderService;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.count.TotalCount;
import com.github.farmplus.web.dto.order.request.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{productId}")
    public ResponseDto purchaseProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @PathVariable("productId") Long productId,
                                       @RequestBody OrderRequest orderRequest) {
        boolean isSuccess = orderService.processOrder(customUserDetails, productId, orderRequest);

        if (isSuccess) {
            return new ResponseDto(HttpStatus.OK.value(), "구매가 성공적으로 완료되었습니다.");
        } else {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "구매 처리에 실패하였습니다.");
        }
    }

    @GetMapping("/list")
    public ResponseDto getOrderList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestParam(defaultValue = "0", value = "page") Integer pageNum) {
        return new ResponseDto(HttpStatus.OK.value(), "구매 목록 조회 성공",
                orderService.getOrderList(customUserDetails, pageNum));
    }

    @GetMapping("/total-count")
    public ResponseDto getTotalOrderCount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        TotalCount totalCount = orderService.getTotalOrderCount(customUserDetails);  // 수정된 메서드 사용
        return new ResponseDto(HttpStatus.OK.value(), "전체 주문 총 개수 조회", totalCount);
    }
}
