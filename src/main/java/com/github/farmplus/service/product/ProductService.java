package com.github.farmplus.service.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.category.CategoryRepository;
import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.partyUser.PartyUserRepository;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import com.github.farmplus.repository.product_discount.ProductDiscountRepository;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.product.response.ProductDetail;
import com.github.farmplus.web.dto.product.response.ProductOption;
import com.github.farmplus.web.dto.product.response.ProductParty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final PartyUserRepository partyUserRepository;

//    public ResponseDto productListResult(String categoryName, String sort, Integer pageNum) {
//
//        Pageable pageable = PageRequest.of(pageNum,10);
//        Page<Product> productList;
//        if (categoryName.equalsIgnoreCase("all")){
//            productList = getProductsBySort(sort,pageable);
//        }else {
//            Category category =categoryRepository.findByCategoryName(categoryName)
//                    .orElseThrow(()-> new NotFoundException(categoryName + "에 해당하는 카테고리가 존재하지 않습니다."));
//            productList = getProductsBySortAndCategory(sort, category, pageable);
//        }
//
//
//    }
//    public Page<Product> getProductsBySort(String sort,Pageable pageable){
//        if (sort.equalsIgnoreCase("createAt")){
//            return productRepository.findAllByCreateAt(pageable);
//        }else if(sort.equalsIgnoreCase("purchaseCount")){
//            return productRepository.findAllOrderByOrderCountDesc(pageable);
//        }
//        else if(sort.equalsIgnoreCase("priceDescending")){
//            return productRepository.findAllByOrderByPriceDesc(pageable);
//        }
//        else if(sort.equalsIgnoreCase("priceAscending")){
//            return productRepository.findAllByOrderByPriceAsc(pageable);
//        }
//        return   productRepository.findAllByCreateAt(pageable);
//
//    }
//    public Page<Product> getProductsBySortAndCategory(String sort, Category category, Pageable pageable) {
//        // 카테고리별로 상품을 정렬
//        if (sort.equalsIgnoreCase("createAt")) {
//            return productRepository.findAllByCategoryOrderByCreateAtDesc(category, pageable);
//        } else if (sort.equalsIgnoreCase("purchaseCount")) {
//            return productRepository.findAllByCategoryOrderByOrderCountDesc(category, pageable);
//        } else if (sort.equalsIgnoreCase("priceDescending")) {
//            return productRepository.findAllByCategoryOrderByPriceDesc(category, pageable);
//        } else if (sort.equalsIgnoreCase("priceAscending")) {
//            return productRepository.findAllByCategoryOrderByPriceAsc(category, pageable);
//        }
//
//        // 기본적으로 생성일 기준 정렬
//        return productRepository.findAllByCategoryOrderByCreateAtDesc(category, pageable);
//    }

    public ResponseDto productDetailResult(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException(productId + "에 해당하는 상품을 찾을 수 없습니다."));
        List<ProductDiscount> productDiscounts = productDiscountRepository.findAllByProductWithDiscount(product);
        List<Discount> discounts = productDiscounts.stream().map(ProductDiscount::getDiscount).toList();
        List<ProductOption> productOptions = discounts.stream().map(ProductOption::of).toList();
        ProductDetail productDetail = ProductDetail.of(product,productOptions);
        return new ResponseDto(HttpStatus.OK.value(),"조회 성공",productDetail);
    }

    public ResponseDto productPartyResult(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException(productId + "에 해당하는 상품을 찾을 수 없습니다."));

        List<Party> productParty = partyRepository.findAllByProductWithUsers(product);
        List<ProductParty> productParties  = productParty.stream().map(ProductParty::of).toList();

        return new ResponseDto(HttpStatus.OK.value(),"조회 성공", productParties);
    }
}
