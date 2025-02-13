//package com.github.farmplus.service.party;
//
//import com.github.farmplus.repository.party.Party;
//import com.github.farmplus.repository.party.PartyRepository;
//import com.github.farmplus.repository.party.PartyStatus;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//    @Slf4j
//    @RequiredArgsConstructor
//    public class PartyTransactionService {
//        private final PartyService partyService;
//        private final PartyRepository partyRepository;
//
//        @Scheduled(cron = "0 */1 * * * *")
//        public void partyStatusScheduled() {
//            // partyService.updateStatus()를 호출하면 트랜잭션이 적용되지 않음
//            // 대신 트랜잭션이 걸린 updateStatus()에서 처리해야 함
//            updateStatus(); // 여기서 트랜잭션이 적용됨
//            List<Party> updatedParties = partyRepository.findAllByStatus(PartyStatus.FAILED);
//            log.info("Updated parties count: {}", updatedParties.size());
//        }
//
//        @Transactional // 여기서 트랜잭션을 관리
//        public void updateStatus() {
//            partyService.updateStatus(); // partyService.updateStatus()를 호출
//        }
//
//
//}
