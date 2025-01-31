package com.github.farmplus.service.party;

import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.discount.DiscountRateRepository;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.party.PartyStatus;
import com.github.farmplus.repository.partyUser.PartyRole;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.partyUser.PartyUserRepository;
import com.github.farmplus.repository.product.Product;
import com.github.farmplus.repository.product.ProductRepository;
import com.github.farmplus.repository.product_discount.ProductDiscount;
import com.github.farmplus.repository.product_discount.ProductDiscountRepository;
import com.github.farmplus.repository.user.User;
import com.github.farmplus.repository.user.UserRepository;
import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.exceptions.BadRequestException;
import com.github.farmplus.service.exceptions.NotFoundException;
import com.github.farmplus.service.exceptions.StockShortageException;
import com.github.farmplus.service.exceptions.UnauthorizedDeleteException;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.party.request.MakeParty;
import com.github.farmplus.web.dto.party.response.MyParty;
import com.github.farmplus.web.dto.party.response.PartyMember;
import com.github.farmplus.web.dto.party.response.PartyMemberName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PartyService {
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final PartyUserRepository partyUserRepository;
    private final ProductRepository productRepository;
    private final DiscountRateRepository discountRateRepository;
    private final ProductDiscountRepository productDiscountRepository;
    public ResponseDto getMyPartyResult(CustomUserDetails customUserDetails, Integer pageNum) {

        User user = tokenUser(customUserDetails);
        log.info("user : " + user);
        Pageable pageable = PageRequest.of(pageNum,10);

        List<PartyUser> partyUserPage = partyUserRepository.findAllByUser(user);

        List<MyParty> myParties =  partyUserPage.stream().map(MyParty::of).collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), myParties.size());
        List<MyParty> pageContent = myParties.subList(start, end);
        Page<MyParty> parties = new PageImpl<>(pageContent, pageable, myParties.size());
        return new ResponseDto(HttpStatus.OK.value(),"조회 성공",parties);

    }
    public ResponseDto makePartyResult(CustomUserDetails customUserDetails, MakeParty makeParty) {
        User user = tokenUser(customUserDetails);
        Integer discountId = makeParty.getOptionId();
        String productName = makeParty.getProductName();
        //상품과 할인 존재하는지 확인
        Product product = findProductByName(productName);
        Discount discount = findDiscountById(discountId);
        ProductDiscount productDiscount = findProductDiscount(product,discount);
        // 등록하려는 파티의 모집인원 수와 구매개수를 곱한 값이 상품의 현재 수량보다 많다면 구매 불가
        Long buyCapacity = (long) makeParty.getCapacity() * discount.getPeople();
        validateStockAvailability(product,buyCapacity);

        Party party = Party.of(product,productDiscount,makeParty);
        Party saveParty = partyRepository.save(party);
        PartyUser partyUser = PartyUser.host(user,saveParty);
        partyUserRepository.save(partyUser);

        return new ResponseDto(HttpStatus.OK.value(),"파티 등록이 되었습니다.");
    }
    @Transactional
    public ResponseDto updatePartyResult(CustomUserDetails customUserDetails, MakeParty makeParty,Long partyId) {
        User user = tokenUser(customUserDetails);
        Party party = findPartyById(partyId);

        //본인 호스트가 아닌 경우 예외처리
        if (!isCheckHost(user,party)){
            throw  new UnauthorizedDeleteException(partyId +"는 유저 " + user.getName() + "이 HOST가 아니어서 변경 불가능합니다.");
        }
        //파티 상태가 실패면 변경 불가능
        if (party.getStatus().equals(PartyStatus.FAILED) || party.getStatus().equals(PartyStatus.COMPLETED) ){
            throw new BadRequestException("해당 파티는 " + PartyStatus.FAILED.getStatusInKorean() +"이거나" +PartyStatus.COMPLETED.getStatusInKorean() +"이므로 변경 불가능합니다.");
        }
        //상품과 할인 존재하는지 확인
        Product product = findProductByName(makeParty.getProductName());
        Discount discount = findDiscountById(makeParty.getOptionId());
        ProductDiscount productDiscount = findProductDiscount(product,discount);
        if (!party.getProduct().equals(product)){
            throw new BadRequestException("상품 변경은 불가능합니다.");
        }
        // 등록하려는 파티의 모집인원 수와 구매개수를 곱한 값이 상품의 현재 수량보다 많다면 구매 불가
        Long buyCapacity = (long) makeParty.getCapacity() * discount.getPeople();
        validateStockAvailability(product,buyCapacity);

        Party updateParty = party.updateDetails(product,productDiscount,makeParty);
        List<PartyUser> partyUsers = partyUserRepository.findAllByParty(updateParty);



        return new ResponseDto(HttpStatus.OK.value(),"파티 정보가 변경되었습니다.");

    }
    /*
    * 토큰에 해당하는 유저 찾기 메소드
    * */
    public User tokenUser(CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        return userRepository.findByEmailFetchJoin(email)
                .orElseThrow(()-> new NotFoundException(email + "에 해당하는 유저가 존재하지 않습니다."));

    }
    /**
     * 상품/할인/상품에 해당하는 할인/파티 찾기 찾기/파티 유저 찾기
     * */
    private Product findProductByName(String productName) {
        return productRepository.findByProductName(productName)
                .orElseThrow(() -> new NotFoundException(productName + "이란 상품은 없습니다."));
    }

    private Discount findDiscountById(Integer discountId) {
        return discountRateRepository.findById(discountId)
                .orElseThrow(() -> new NotFoundException(discountId + "에 해당하는 옵션은 없습니다."));
    }

    private ProductDiscount findProductDiscount(Product product, Discount discount) {
        return productDiscountRepository.findByProductAndDiscount(product, discount)
                .orElseThrow(() -> new NotFoundException(product.getProductName() + "은 " + discount.getPeople() + "명 이벤트가 적용되지 않는 상품입니다."));
    }
    private Party findPartyById(Long partyId){
        return partyRepository.findById(partyId)
                .orElseThrow(()-> new NotFoundException(partyId + "에 해당하는 파티가 없습니다."));
    }
    /**
     * 파티를 업그레이드하거나 삭제할 때 본인이 호스트가 맞느지 확인
     * */
    private Boolean isCheckHost(User user, Party party){

        PartyUser partyUser = partyUserRepository.findAllByParty(party).stream()
                .filter((pu) -> pu.getPartyRole().equals(PartyRole.HOST)).findFirst()
                .orElseThrow(()-> new NotFoundException("해당 파티에는 HOST가 없어 확인 불가능합니다."));
        if (!partyUser.getUser().equals(user)){
            return false;
        }
        return true;
    }
    /**
     *
     * */
    private void validateStockAvailability(Product product, long buyCapacity) {
        if (buyCapacity > product.getStock()) {
            throw new StockShortageException(
                    String.format("현재 상품의 수량은 %d개 입니다. 등록하려는 파티의 구매 수는 %d개 이므로 파티 등록이 불가합니다.",
                            product.getStock(), buyCapacity)
            );
        }
    }





}
