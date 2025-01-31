package com.github.farmplus.web.dto.party.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class PartyMemberName {
    private final String name;
}
