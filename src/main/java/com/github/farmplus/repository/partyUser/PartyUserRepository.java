package com.github.farmplus.repository.partyUser;

import com.github.farmplus.repository.party.Party;
import com.github.farmplus.repository.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser,Long> {
    List<PartyUser> findAllByParty(Party party);
    @Query("SELECT DISTINCT pu FROM PartyUser pu " +
            "JOIN FETCH pu.party p " +
            "JOIN FETCH p.product " +
            "JOIN FETCH pu.user " +
            "LEFT JOIN FETCH p.partyUserList " +
            "WHERE pu.user = :user")
    List<PartyUser> findAllByUser(User user);

    @Query("SELECT new com.github.farmplus.repository.partyUser.PartyUserAmount(pu.user.userId, pu.paymentAmount, pu.user.money, pu.paymentAmount + pu.user.money) " +
            "FROM PartyUser pu WHERE pu.party = :party")
    List<PartyUserAmount> findPartyUserAmounts(@Param("party") Party party);

    void deleteByUserAndParty(User user,Party party);
    Optional<PartyUser> findByUserAndParty(User user, Party party);

}
