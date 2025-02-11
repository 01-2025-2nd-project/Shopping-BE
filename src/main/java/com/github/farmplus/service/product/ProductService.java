package com.github.farmplus.service.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.category.CategoryRepository;
import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.partyUser.PartyUserRepository;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product.ProductWithOrderAndParty;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import com.github.farmplus.repository.product_discount.ProductDiscountRepository;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.count.TotalCount;
import com.github.farmplus.web.dto.product.response.ProductDetail;
import com.github.farmplus.web.dto.product.response.ProductMain;
import com.github.farmplus.web.dto.product.response.ProductOption;
import com.github.farmplus.web.dto.product.response.ProductParty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    public ResponseDto productListResult(String categoryName, String sort, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum,10);
        Page<ProductMain> productList;
        if (categoryName.equalsIgnoreCase("all")){
            productList = getProductsBySort(sort,pageable);
        }else {
            Category category =categoryRepository.findByCategoryName(categoryName)
                    .orElseThrow(()-> new NotFoundException(categoryName + "에 해당하는 카테고리가 존재하지 않습니다."));
            productList = getProductsBySortAndCategory(sort, category, pageable);
        }
        return new ResponseDto(HttpStatus.OK.value(),"조회 성공", productList);

    }
    public Page<ProductMain> getProductsBySort(String sort,Pageable pageable){
        Page<ProductWithOrderAndParty> products;
        if (sort.equalsIgnoreCase("createAt")){
            products = productRepository.findAllByOrderByCreateAtDesc(pageable);
            return products.map(ProductMain::of);
        }else if(sort.equalsIgnoreCase("purchaseCount")){
            products = productRepository.findAllOrderByOrderCountDesc(pageable);
            return products.map(ProductMain::of);
        }
        else if(sort.equalsIgnoreCase("priceDescending")){
            products= productRepository.findAllByOrderByPriceDesc(pageable);
            return products.map(ProductMain::of);
        }
        else if(sort.equalsIgnoreCase("priceAscending")){
            products = productRepository.findAllByOrderByPriceAsc(pageable);
            return products.map(ProductMain::of);
        }
        products = productRepository.findAllByOrderByCreateAtDesc(pageable);
        return products.map(ProductMain::of);

    }
    public Page<ProductMain> getProductsBySortAndCategory(String sort, Category category, Pageable pageable) {
        // 카테고리별로 상품을 정렬
        Page<ProductWithOrderAndParty> products;
        if (sort.equalsIgnoreCase("createAt")) {
            products = productRepository.findAllByCategoryOrderByCreateAtDesc(category, pageable);
            return products.map(ProductMain::of);
        } else if (sort.equalsIgnoreCase("purchaseCount")) {
            products = productRepository.findAllByCategoryOrderByOrderCountDesc(category, pageable);
            return products.map(ProductMain::of);

        } else if (sort.equalsIgnoreCase("priceDescending")) {
            products =productRepository.findAllByCategoryOrderByPriceDesc(category, pageable);
            return products.map(ProductMain::of);
        } else if (sort.equalsIgnoreCase("priceAscending")) {
            products =productRepository.findAllByCategoryOrderByPriceAsc(category, pageable);
            return products.map(ProductMain::of);
        }

        // 기본적으로 생성일 기준 정렬
        products= productRepository.findAllByCategoryOrderByCreateAtDesc(category, pageable);
        return products.map(ProductMain::of);
    }
    @Cacheable(value = "productDetail" , key = "#productId")
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


    public ResponseDto productTotalCountResult() {
        Integer productTotalCount = productRepository.findProductTotalCount();
        TotalCount totalCount = TotalCount.of(productTotalCount);
        return new ResponseDto(HttpStatus.OK.value(),"상품 총 개수 조회 성공" ,totalCount);
    }
    @Cacheable(value = "productTotalCount",key = "#category")
    public ResponseDto productTotalCountByCategoryResult(String category) {
        if (category.equals("all")){
           return productTotalCountResult();
        }else {
            Integer productTotalCount = productRepository.findProductByCategoryTotalCount(category);
            TotalCount totalCount = TotalCount.of(productTotalCount);
            return new ResponseDto(HttpStatus.OK.value(),"상품 총 개수 조회 성공" ,totalCount);
        }

    }


    public ResponseDto searchProduct(String keyword, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum,10);
        Page<ProductWithOrderAndParty> products = productRepository.findSearchProduct(keyword,pageable);
        Page<ProductMain> productMains = products.map(ProductMain::of);
        return new ResponseDto(HttpStatus.OK.value(),"검색 결과 조회 성공", productMains);
    }
}
