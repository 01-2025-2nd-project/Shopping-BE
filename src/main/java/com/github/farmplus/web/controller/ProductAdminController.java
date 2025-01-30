package com.github.farmplus.web.controller;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.product.ProductAdminService;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.product.request.ProductRegister;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/admin")
@RequiredArgsConstructor
@Slf4j
public class ProductAdminController {
    private final ProductAdminService productAdminService;

    @GetMapping("/category")
    public ResponseDto getCategory(){
        return productAdminService.getCategoryResult();
    }
    @PostMapping
    public ResponseDto productRegister(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @RequestBody ProductRegister productRegister){
        return productAdminService.productRegisterResult(customUserDetails,productRegister);
    }
    @PutMapping("/{productId}")
    public ResponseDto productUpdate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody ProductRegister productRegister,
                                     @PathVariable("productId") Long productId){
        return productAdminService.productUpdateResult(customUserDetails,productRegister,productId);
    }
    @DeleteMapping("/{productId}")
    public ResponseDto productDelete(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @PathVariable("productId") Long productId){
        return productAdminService.productDeleteResult(customUserDetails,productId);

    }



}
