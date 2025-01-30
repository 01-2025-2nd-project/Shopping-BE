package com.github.farmplus.repository.partyUser;

import com.github.farmplus.repository.party.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser,Long> {
    List<PartyUser> findAllByParty(Party party);
}
