package com.github.farmplus.web.controller;

import com.github.farmplus.service.product.ProductService;
import com.github.farmplus.web.dto.base.ResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
//    @GetMapping
//    public ResponseDto productList(@RequestParam(required = false, defaultValue = "all",value = "category") String categoryName,
//                                   @RequestParam(required = false, defaultValue = "createAt",value = "sort") String sort,
//                                   @RequestParam(required = false, defaultValue = "0", value = "page")Integer pageNum){
//        return productService.productListResult(categoryName,sort,pageNum);
//    }
    @GetMapping("/{productId}")
    public ResponseDto productDetail(@PathVariable("productId") Long productId){
        return productService.productDetailResult(productId);

    }
    @GetMapping("/{productId}/party")
    public ResponseDto productParty(@PathVariable("productId") Long productId){
        return productService.productPartyResult(productId);
    }
}
