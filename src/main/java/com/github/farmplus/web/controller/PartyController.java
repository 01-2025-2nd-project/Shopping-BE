package com.github.farmplus.web.controller;

import com.github.farmplus.repository.userDetails.CustomUserDetails;
import com.github.farmplus.service.party.PartyService;
import com.github.farmplus.web.dto.base.ResponseDto;
import com.github.farmplus.web.dto.party.request.MakeParty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party")
@Slf4j
public class PartyController {
    private final PartyService partyService;

    @GetMapping("/list")
    public ResponseDto getMyParty(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @RequestParam(required = false, defaultValue = "0" , value = "page")Integer pageNum){

        return partyService.getMyPartyResult(customUserDetails, pageNum);
    }
    @PostMapping
    public ResponseDto makeParty(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                 @RequestBody MakeParty makeParty){
        return partyService.makePartyResult(customUserDetails,makeParty);
    }
    @PutMapping("/{partyId}")
    public ResponseDto updateParty(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestBody MakeParty makeParty,
                                   @PathVariable("partyId")Long partyId){
        return partyService.updatePartyResult(customUserDetails,makeParty,partyId);
    }
}
