package com.github.farmplus.web.dto.product.response;

import com.github.farmplus.repository.party.Party;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.relational.core.sql.In;

@ToString
@Builder
@Getter
public class ProductParty {
    private final Long partyId;
    private final String partyName;
    private final Integer option;
    private final Integer joinCount;
    private final Integer capacity;
    private final String status;

    public static ProductParty of(Party party){

        return ProductParty.builder()
                    .partyId(party.getPartyId())
                    .partyName(party.getPartyName())
                    .option(party.getProductDiscount().getDiscount().getPeople())
                    .joinCount(party.getPartyUserList().size())
                    .capacity(party.getCapacity())
                    .status(party.getStatus().getStatusInKorean())
                    .build();
    }
}
