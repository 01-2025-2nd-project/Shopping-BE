package com.github.farmplus.web.dto.party.response;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.partyUser.PartyUser;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@Builder
public class MyParty {
    private final Long partyId;
    private final String partyName;
    private final String productName;
    private final Integer purchaseCount;
    private final Long optionId;
    private final Integer joinCount;
    private final String status;
    private final LocalDate endDate;
    private final PartyMember partyMember;

    public static MyParty of(PartyUser partyUser){
        Party party = partyUser.getParty();

        PartyMember partyMember = PartyMember.of(party.getPartyUserList());

        return MyParty.builder()
                .partyId(party.getPartyId())
                .partyName(party.getPartyName())
                .productName(party.getProduct().getProductName())
                .purchaseCount(party.getCapacity())
                .joinCount(party.getPartyUserList().size())
                .status(party.getStatus().toString())
                .endDate(party.getEndDate())
                .partyMember(partyMember)
                .build();

    }

}
