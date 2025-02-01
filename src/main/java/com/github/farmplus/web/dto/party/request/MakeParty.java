package com.github.farmplus.web.dto.party.request;

import com.github.farmplus.service.exceptions.InvalidDateException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString

public class MakeParty {
    private final String partyName;
    private final Integer optionId;
    private final String productName;
    private final LocalDate endDate;
    private final Integer capacity;
    @Builder
    public MakeParty(String partyName,Integer optionId, String productName, LocalDate endDate, Integer capacity){
        LocalDate now = LocalDate.now();
        if (endDate.isBefore(now) || endDate.isEqual(now)){
            throw new InvalidDateException("현재날짜이거나 현재 날짜보다 이전입니다.");
        }
        this.partyName=partyName;
        this.optionId=optionId;
        this.productName = productName;
        this.endDate=endDate;
        this.capacity=capacity;
    }
}
