package com.github.farmplus.service.party;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.party.PartyRepository;
import com.github.farmplus.repository.party.PartyStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
    @Slf4j
    @RequiredArgsConstructor
    public class PartyTransactionService {
        private final PartyService partyService;
        private final PartyRepository partyRepository;


    @Scheduled(cron = "0 */1 * * * *")
    public void schedulePartyStatus(){
        partyService.updateStatus();
    }




}
