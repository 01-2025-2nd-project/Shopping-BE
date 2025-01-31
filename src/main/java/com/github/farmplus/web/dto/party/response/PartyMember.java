package com.github.farmplus.web.dto.party.response;

import com.github.farmplus.repository.partyUser.PartyRole;
import com.github.farmplus.repository.partyUser.PartyUser;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class PartyMember {
    private final String partyMasterName;
    private final List<PartyMemberName> partyMemberName;
    public static PartyMember of(List<PartyUser> partyUsers) {
        String masterName = partyUsers.stream()
                .filter(pu -> pu.getPartyRole() == PartyRole.HOST)
                .findFirst()
                .map(pu -> pu.getUser().getName())
                .orElse("Unknown");

        List<PartyMemberName> memberNames = partyUsers.stream()
                .filter(pu -> pu.getPartyRole() != PartyRole.HOST)
                .map(pu -> PartyMemberName.builder().name(pu.getUser().getName()).build())
                .collect(Collectors.toList());

        return PartyMember.builder()
                .partyMasterName(masterName)
                .partyMemberName(memberNames)
                .build();
    }

}
