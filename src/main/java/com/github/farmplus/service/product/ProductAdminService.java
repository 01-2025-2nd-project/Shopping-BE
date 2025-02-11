package com.github.farmplus.service.product;

import com.github.farmplus.repository.category.Category;
import com.github.farmplus.repository.category.CategoryRepository;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.role.Role;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.repository.userRole.UserRole;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.product.response.CategoryResponse;
import com.github.farmplus.web.dto.product.request.ProductRegister;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdminService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    @Cacheable(value = "categoryList", key = "#root.methodName")
    public ResponseDto getCategoryResult() {
       List<Category> categories =categoryRepository.findAll();
       if (categories.isEmpty()){
           throw new NotFoundException("카테고리 리스트가 DB에 존재하지 않습니다.");
       }
       List<String> categoryNames = categories.stream().map(Category::getCategoryName).toList();
       List<CategoryResponse> categoryResponses = categoryNames.stream().map(CategoryResponse::of).toList();
       return new ResponseDto(HttpStatus.OK.value(),"카테고리 리스트 조회 성공",categoryResponses);
    }


    @CacheEvict(value = "productTotalCount", allEntries = true)
    public ResponseDto productRegisterResult(CustomUserDetails customUserDetails, ProductRegister productRegister) {
        //유저 찾기 메소드로 유저 찾기
        User tokenUser = findUserByEmail(customUserDetails);
        //접근하는 유저 역할 추출
        if (!isAdmin(tokenUser)){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        }
        //관리자이면 실행
        Category category = categoryRepository.findByCategoryName(productRegister.getCategoryName())
                .orElseThrow(()-> new NotFoundException(productRegister.getCategoryName()+"는 존재하지 않는 카테고리입니다."));
        Product product = Product.of(productRegister,category);
        productRepository.save(product);

        return new ResponseDto(HttpStatus.CREATED.value(),product.getProductName()+"이 정상적으로 등록되었습니다.");
    }


    @CacheEvict(value = "productDetail", key = "#productId")
    @Transactional
    public ResponseDto productUpdateResult(CustomUserDetails customUserDetails, ProductRegister productRegister, Long productId) {
        User tokenUser = findUserByEmail(customUserDetails);

        if (!isAdmin(tokenUser)){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        }
        Category category = categoryRepository.findByCategoryName(productRegister.getCategoryName())
                .orElseThrow(()-> new NotFoundException(productRegister.getCategoryName()+"는 존재하지 않는 카테고리입니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("에 해당하는 상품을 찾을 수 없습니다."));
        product.updateFromRegister(productRegister,category);
        return new ResponseDto(HttpStatus.OK.value(), productId+"에 해당하는 상품이 업데이트 완료되었습니다.");
    }
    @CacheEvict(value = "productDetail", key = "#productId")
    public ResponseDto productDeleteResult(CustomUserDetails customUserDetails, Long productId) {
        User tokenUser = findUserByEmail(customUserDetails);
        if (!isAdmin(tokenUser)){
            throw new AccessDeniedException("관리자만 접근할 수 있습니다.");
        }
        productRepository.deleteById(productId);
        return new ResponseDto(HttpStatus.OK.value(),productId+"에 해당하는 상품이 삭제되었습니다..");
    }


    public boolean isAdmin(User user) {
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getRoleName)
                .anyMatch(role -> role.toLowerCase().contains("role_admin")); // "admin"이 포함된 경우 허용
    }
    //이메일로 유저찾는 메소드
    public User findUserByEmail(CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        return userRepository.findByEmailFetchJoin(email)
                .orElseThrow(()-> new NotFoundException(email + "에 해당하는 유저를 찾을 수 없습니다."));

    }


}
