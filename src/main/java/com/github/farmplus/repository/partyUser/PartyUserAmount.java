package com.github.farmplus.repository.partyUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class PartyUserAmount {
    private Integer userId;
    private Double paymentAmount;
    private Double userMoney;
    private Double totalAmount;
}
