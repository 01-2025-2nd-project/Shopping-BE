package com.github.farmplus.web.controller;

import com.github.farmplus.repository.user.JwtUser;
import com.github.farmplus.repository.user.User;
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
    public ResponseDto getMyParty(@JwtUser User user,
                                  @RequestParam(required = false, defaultValue = "0" , value = "page")Integer pageNum){

        return partyService.getMyPartyResult(user, pageNum);
    }
    @PostMapping
    public ResponseDto makeParty(@JwtUser User user,
                                 @RequestBody MakeParty makeParty){
        return partyService.makePartyResult(user,makeParty);
    }
    @PutMapping("/{partyId}")
    public ResponseDto updateParty(@JwtUser User user,
                                    @RequestBody MakeParty makeParty,
                                   @PathVariable("partyId")Long partyId){
        return partyService.updatePartyResult(user,makeParty,partyId);
    }
    @DeleteMapping("/{partyId}")
    public ResponseDto deleteParty(@JwtUser User user,
                                   @PathVariable("partyId")Long partyId){
        return partyService.deletePartyResult(user,partyId);
    }
    @PostMapping("/{partyId}/join")
    public ResponseDto joinParty(@JwtUser User user,
                                 @PathVariable("partyId") Long partyId){
        return partyService.joinPartyResult(user,partyId);
    }
    @DeleteMapping("/{partyId}/join")
    public ResponseDto deleteJoinParty(@JwtUser User user,
                                       @PathVariable("partyId") Long partyId){
        return partyService.deleteJoinPartyResult(user,partyId);
    }
    @GetMapping("/total")
    public ResponseDto partyTotalCount(){
        return partyService.partyTotalCountResult();
    }
    @GetMapping("/my-party")
    public ResponseDto myPartyTotalCount(@JwtUser User user){
        return partyService.myPartyTotalCountResult(user);
    }
}
