package com.github.farmplus.web.dto.product.response;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.partyUser.PartyRole;
import com.github.farmplus.repository.partyUser.PartyUser;
import com.github.farmplus.service.exceptions.NotFoundException;
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
    private final String partyMaster;
    private final Integer joinCount;
    private final Integer capacity;
    private final String status;

    public static ProductParty of(Party party){

        return ProductParty.builder()
                    .partyId(party.getPartyId())
                    .partyName(party.getPartyName())
                    .option(party.getProductDiscount().getDiscount().getPeople())
                    .partyMaster(party.getPartyUserList().stream().filter((pu)-> pu.getPartyRole().equals(PartyRole.HOST)).map((pu)->pu.getUser().getName()).findFirst().orElseThrow(()-> new NotFoundException(" 파티 마스터가 없습니다.")))
                    .joinCount(party.getPartyUserList().size())
                    .capacity(party.getCapacity())
                    .status(party.getStatus().getStatusInKorean())
                    .build();
    }
}
