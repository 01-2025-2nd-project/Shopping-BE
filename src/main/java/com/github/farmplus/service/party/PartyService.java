package com.github.farmplus.service.party;

import com.github.farmplus.repository.discount.Discount;
import com.github.farmplus.repository.discount.DiscountRateRepository;
import com.github.farmplus.repository.notification.Notification;
import com.github.farmplus.repository.notification.NotificationRepository;
import com.github.farmplus.repository.order.Order;
import com.github.farmplus.repository.order.OrderRepository;
import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.party.PartyStatus;
import com.github.farmplus.repository.partyUser.PartyRole;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.repository.partyUser.PartyUserAmount;
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
import com.github.farmplus.web.dto.count.TotalCount;
import com.github.farmplus.web.dto.notification.NotificationDto;
import com.github.farmplus.web.dto.party.request.MakeParty;
import com.github.farmplus.web.dto.party.response.MyParty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    public ResponseDto getMyPartyResult(CustomUserDetails customUserDetails, Integer pageNum) {

        User user = tokenUser(customUserDetails);
        log.info("user : " + user);
        Pageable pageable = PageRequest.of(pageNum,10);

        Page<PartyUser> partyUserPage = partyUserRepository.findAllByUser(user,pageable);

        Page<MyParty> myParties =  partyUserPage.map(MyParty::of);

        return new ResponseDto(HttpStatus.OK.value(),"조회 성공",myParties);

    }

    @Transactional
    public ResponseDto deletePartyResult(CustomUserDetails customUserDetails, Long partyId) {
        //토큰으로 유저 찾기
        User user = tokenUser(customUserDetails);
        log.info("유저 찾기");
        //파티 찾기
        Party party = findPartyById(partyId);
        log.info("파티 찾기");
        //삭제하려는 유저가 파티장인지 확인
        Boolean isHost = isCheckHost(user,party);
        if (!isHost){
            throw new UnauthorizedDeleteException(partyId +"는 유저 " + user.getName() + "이 HOST가 아니어서 변경 불가능합니다.");
        }
        log.info("host인지 확인");
        // 파티 상태에 따라 다르기
        // 1) 완료 실패인 경우 : 파티 삭제 시 order부분에 partyId null(완료인 경우만)로 지정, PartyUser리스트 지우기, Party지우기
        // 2) 진행 중인 경우 : 파티 삭제 시 유저에게 money 다시, PartyUser리스트 지우기, Party지우기
        deletePartyUpdateByPartyStatus(party);

        return new ResponseDto(HttpStatus.OK.value(),"파티가 삭제되었습니다.");
    }
    @Transactional
    public ResponseDto makePartyResult(CustomUserDetails customUserDetails, MakeParty makeParty) {
        User user = tokenUser(customUserDetails);
        log.info("유저 : " +user);
        Integer discountId = makeParty.getOptionId();
        String productName = makeParty.getProductName();
        //상품과 할인 존재하는지 확인
        Product product = findProductByName(productName);
        Discount discount = findDiscountById(discountId);
        ProductDiscount productDiscount = findProductDiscount(product,discount);
        // 등록하려는 파티의 모집인원 수와 구매개수를 곱한 값이 상품의 현재 수량보다 많다면 구매 불가
        Long buyCapacity = (long) makeParty.getPurchaseCount() * discount.getPeople();
        validateStockAvailability(product,buyCapacity);
        Double salePrice = product.getPrice() -( product.getPrice() * discount.getDiscountRate());
        Double saleTotalPrice = salePrice * makeParty.getPurchaseCount();
//        if ( saleTotalPrice > user.getMoney() ){
//            throw new BadRequestException("현재 구매하려는 가격 : " +saleTotalPrice +" 현재 가지고 있는 돈 : " + user.getMoney() +"이므로 파티 등록이 불가능합니다.");
//        }
        //동시성이 발생하지 않게
        //여러 개의 요청이 들어와도 UPDATE가 원자적으로 실행됨
        //WHERE money >= amount 덕분에 잔액 부족 시 업데이트 방지됨
        //user.updateMoney()처럼 엔티티를 가져와 변경하는 방식은 JPA의 변경 감지를 사용하므로,
        //여러 요청이 동시에 들어오면 낙관적 락(Optimistic Lock) 충돌 가능성이 있음
        //바로 UPDATE하는 방식이 더 빠르고 안정적
        int updatedRows = userRepository.deductMoney(user.getUserId(), saleTotalPrice);
        if (updatedRows == 0) {
            throw new BadRequestException("잔액 부족 또는 동시성 문제 발생");
        }
        Party party = Party.of(product,productDiscount,makeParty);
        Party saveParty = partyRepository.save(party);
        PartyUser partyUser = PartyUser.host(user,saveParty,saleTotalPrice);
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
        Long buyCapacity = (long) makeParty.getPurchaseCount() * discount.getPeople();
        validateStockAvailability(product,buyCapacity);

        Double salePrice = product.getPrice() -( product.getPrice() * discount.getDiscountRate());
        final Double saleTotalPrice = salePrice * makeParty.getPurchaseCount();

        List<PartyUserAmount> partyUsers = partyUserRepository.findPartyUserAmounts(party);
        log.info("partyUser : " + partyUsers);
        checkPartyUserMoney(partyUsers,saleTotalPrice);

        Double payment = partyUsers.get(0).getPaymentAmount();
        //유저 돈에 더해줄 가격
        //바꾸려는 가격이 지불한 금액보다 작은 경우
        final Double userMoney =checkUserMoney(saleTotalPrice,payment);

        List<PartyUser> partyUserList = partyUserRepository.findAllByParty(party);

        updatePartyUserMoney(partyUserList,saleTotalPrice,userMoney);

        party.updateDetails(product,productDiscount,makeParty);

        return new ResponseDto(HttpStatus.OK.value(),"파티 정보가 변경되었습니다.");

    }
    @Transactional
    @CacheEvict(value = "notificationList", key = "#customUserDetails.userId")
    public ResponseDto joinPartyResult(CustomUserDetails customUserDetails, Long partyId) {
        User user =tokenUser(customUserDetails);
        Party party = partyRepository.findByIdWithLock(partyId)
                .orElseThrow(()-> new NotFoundException(partyId + "에 해당하는 파티를 찾을 수 없습니다."));
        List<PartyUser> partyUsers = partyUserRepository.findAllByParty(party);
        ProductDiscount productDiscount = party.getProductDiscount();
        Discount discount = productDiscount.getDiscount();
        Double payment = partyUsers.get(0).getPaymentAmount();
        //파티 참여 시 파티 상태가 실패/완료면 참여 불가
        checkPartyStatus(party);
        //유저 머니 검사
        joinPartyByUserMoneyCheck(user,payment);
        //해당 파티에 이미 참여한 파티 유저인지 확인
        isCheckAlreadyParty(user,party);
        //파티 참여 시 유저 머니 차감
        //파티 참여 시 마지막 파티원이면 자동으로 구매 유저 머니 차감
        //파티 참여시 동시성 고려
        int updatedRows = userRepository.deductMoney(user.getUserId(), payment);
        if (updatedRows == 0) {
            throw new BadRequestException("잔액 부족 또는 동시성 문제 발생");
        }
        PartyUser joinPartyUser = PartyUser.member(user,party,payment);
        partyUserRepository.save(joinPartyUser);
        if ( partyUsers.size() == (discount.getPeople() - 1) ){
            //동시성 문제로 인해 Lock 적용
            isCheckProductStock(party,discount);
            List<PartyUser> updatePartyUserList = partyUserRepository.findAllByParty(party);
            log.info("구매로 넘어가기 전");
            List<Order> orders = updatePartyUserList.stream().map(Order::of).toList();
            //구매로 넘어갈 시 동시성 고려
            log.info("구매로 넘어간 후");

            party.updatePartyStatus(PartyStatus.COMPLETED);
            orderRepository.saveAll(orders);
            // 알림 생성 및 전송
            sendNotifications(party, updatePartyUserList);

            return new ResponseDto(HttpStatus.CREATED.value(),"파티 마지막 참여자이므로 결제 완료되었습니다..");
        }


        return new ResponseDto(HttpStatus.CREATED.value(),"파티 참여에 성공했습니다.");
    }
    // 알림 생성 후 WebSocket으로 전송

    private void sendNotifications(Party party, List<PartyUser> partyUsers) {
        partyUsers.stream()
                .map(Notification::from)  // PartyUser를 Notification으로 변환
                .peek(notificationRepository::save) // 알림 저장
                .map(NotificationDto::from)  // Notification을 NotificationDto로 변환
                .forEach(notificationDto -> messagingTemplate.convertAndSend("/topic/notifications/" + notificationDto.getEmail(), notificationDto));
    }
    @Transactional
    public ResponseDto deleteJoinPartyResult(CustomUserDetails customUserDetails, Long partyId) {
        User user = tokenUser(customUserDetails);
        Party party = findPartyById(partyId);
        List<PartyUser> partyUsers = partyUserRepository.findAllByParty(party);
        List<User> partyUserList = partyUsers.stream().map(PartyUser::getUser).toList();
        //파티에 유저가 가입되어 있는지 확인
        if (!partyUserList.contains(user)){
            throw new NotFoundException("해당 파티에 유저 : " + user.getName() +"는(은) 가입되어 있지 않습니다.");
        }
        //파티장은 탈퇴가 아닌 직접 파티를 삭제하도록 예외 안내 메시지처리(why? : 파티원들이 있는데 탈퇴하는 건 안되는 일이기 때문이다)
        PartyUser partyUser = partyUserRepository.findByUserAndParty(user,party)
                .orElseThrow(()-> new NotFoundException("파티 : " + party.getPartyName() + "에 참여하지 않았습니다."));
        if (partyUser.getPartyRole().equals(PartyRole.HOST)){
            throw new BadRequestException("HOST는 파티 나가기가 아닌 파티 삭제를 이용해주세요");
        }
        //파티 상태 확인
        checkPartyStatus(party);
        //파티 탈퇴 설계
        //해당 파티에서 PartyUser리스트에서 해당 유저 지우기
        partyUserRepository.deleteByUserAndParty(user,party);
        //원래 돈 복귀시키기
        //동시성 고려
        int updatedRows = userRepository.updateMoney(user.getUserId(),partyUsers.get(0).getPaymentAmount() );
        if (updatedRows == 0) {
            throw new BadRequestException("잔액 업데이트 실패");
        }
        return new ResponseDto(HttpStatus.OK.value(),"파티 탈퇴가 되었습니다.");


    }
    /**
     * --------------------------------메소드----------------------------------------------------
     * */
    /**
     * 계산한 userMoney 업데이트 시키기
     * */
    private void updatePartyUserMoney(List<PartyUser> partyUserList,Double userMoney,Double saleTotalPrice){
        partyUserList.forEach(partyUser -> {
            User updateUser = partyUser.getUser();

            // User의 money 업데이트
            updateUser.updateMoney(updateUser.getMoney() + userMoney);

            int updatedRows = userRepository.updateMoney(updateUser.getUserId(), userMoney);
            if (updatedRows == 0) {
                throw new BadRequestException("잔액 업데이트 실패");
            }

            // PartyUser의 paymentAmount 업데이트
            partyUser.updatePaymentAmount(saleTotalPrice);
        });

    }

    /**
     *  변경 사항에 따라 userMoney 더해줄 값 return하는 메소드
     * */
    private Double checkUserMoney(Double saleTotalPrice, Double payment){
        Double userMoney;
        if (saleTotalPrice < payment){
            userMoney = payment - saleTotalPrice;
        }
        //바꾸려는 가격이 지불 금액보다 큰 경우
        else if (saleTotalPrice > payment){
            userMoney = saleTotalPrice - payment;
        }
        else{
            userMoney = 0.0;
        }
        return userMoney;
    }

    /**
     * 파티원 중 금액이 부족한 사람이 있는지 확인하는 메소드
     * */

    private void checkPartyUserMoney(List<PartyUserAmount> partyUsers, Double saleTotalPrice){
        boolean isInsufficientFunds = partyUsers.stream()
                .anyMatch(partyUser -> partyUser.getTotalAmount() < saleTotalPrice);

        if (isInsufficientFunds) {
            throw new BadRequestException("파티원 중 한 명이 금액이 부족하여 파티 업데이트가 불가능합니다.");
        }
    }

    /*
    * 토큰에 해당하는 유저 찾기 메소드
    * */
    public User tokenUser(CustomUserDetails customUserDetails){
        log.info("tokenUser 메서드 시작");
        String email = customUserDetails.getUsername();
        User user = userRepository.findByEmailFetchJoin(email)
                .orElseThrow(()-> new NotFoundException(email + "에 해당하는 유저가 존재하지 않습니다."));
        log.info("tokenUser 메서드 끝");
        return  user;
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



    /**
     * 파티상태에 따라 삭제 메소드
     * */
    public void deletePartyUpdateByPartyStatus(Party party){
        List<PartyUser> partyUserList = partyUserRepository.findAllByParty(party);

        switch (party.getStatus()){
            case COMPLETED -> {
                List<Order> ordersByParty = orderRepository.findAllByParty(party);
                ordersByParty.stream().forEach((order)->order.updateParty(null));
            }
            case RECRUITING -> {
                Double payment =partyUserList.get(0).getPaymentAmount();
                List<User> userList = partyUserList.stream().map(PartyUser::getUser).toList();
                userList.stream().forEach((user)->user.updateMoney( user.getMoney() + payment));
            }
        }
        partyUserRepository.deleteAll(partyUserList);
        partyRepository.delete(party);

    }


    /**
     * 현재 상품 수량과 비교하기
     * */


    @Transactional
    public void isCheckProductStock(Party party, Discount discount) {
        Product product = productRepository.findByIdWithLock(party.getProduct().getProductId())
                .orElseThrow(() -> new NotFoundException("파티에 해당하는 상품을 찾을 수 없습니다."));

        long requiredStock = (long) party.getCapacity() * discount.getPeople();
        if (requiredStock > product.getStock()) {
            party.updatePartyStatus(PartyStatus.FAILED);
            throw new StockShortageException("현재 상품 " + product.getProductName() + "은(는) 재고 부족으로 구매가 불가합니다.");
        }

        // 재고 업데이트
        product.updateStock(product.getStock() - requiredStock);
    }
    /**
     * 이미 참여한 파티인지 확인
     * */
    private void isCheckAlreadyParty(User user, Party party){
        List<PartyUser> partyUsers = partyUserRepository.findAllByParty(party);
        List<User> users = partyUsers.stream().map(PartyUser::getUser).toList();
        if (users.contains(user)){
            throw new BadRequestException("이미 참여한 파티입니다.");
        }
    }
    /**
     * 파티 상태 확인
     * */
    private void checkPartyStatus(Party party){
        if (!party.getStatus().equals(PartyStatus.RECRUITING)){
            throw new BadRequestException("완료 또는 실패한 파티에는 참여 또는 나가기가 불가능 합니다.");
        }
    }
    /**
     * 참여하려는 파티 유저 돈 체크
     * */
    private void joinPartyByUserMoneyCheck(User user, Double payment){
        if (user.getMoney() < payment){
            throw new BadRequestException("현재 지불해야 하는 금액 : " + payment + "/ user 금액 : " + user.getMoney() + "이므로 파티 참여 불가능합니다.");
        }
    }


    public ResponseDto partyTotalCountResult() {
        Integer partyTotalCount = partyRepository.findPartyTotalCount();
        TotalCount totalCount = TotalCount.of(partyTotalCount);
        return new ResponseDto(HttpStatus.OK.value(),"파티 총 개수 조회 성공" ,totalCount);
    }

    public ResponseDto myPartyTotalCountResult(CustomUserDetails customUserDetails) {

        Integer myPartyTotalCount = partyRepository.findMyPartyTotalCount(customUserDetails.getUserId());
        TotalCount totalCount = TotalCount.of(myPartyTotalCount);
        return new ResponseDto(HttpStatus.OK.value(),"파티 총 개수 조회 성공" ,totalCount);
    }
}
