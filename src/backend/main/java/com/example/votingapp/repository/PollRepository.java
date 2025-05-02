package com.example.votingapp.repository;

import com.example.votingapp.model.Campaign;
import com.example.votingapp.model.Poll;
import com.example.votingapp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    @EntityGraph(attributePaths = {"participants"})
    List<Poll> findByCampaign_Id(Long campaignId);

    @Query("SELECT p.id FROM Poll p JOIN p.participants u WHERE u.email = :email")
    List<Long> findPollIdsByParticipantEmail(@Param("email") String email);

    @Query("SELECT p FROM Poll p JOIN FETCH p.participants WHERE p.campaign.id = :campaignId")
    List<Poll> findByCampaignIdWithParticipants(@Param("campaignId") Long campaignId);

    @Query("SELECT p FROM Poll p JOIN FETCH p.participants JOIN FETCH p.candidates WHERE p.campaign.id = :campaignId")
    List<Poll> findByCampaignIdWithParticipantsAndCandidates(@Param("campaignId") Long campaignId);

    List<Poll> findByParticipantsContains(User user);

    @Query("SELECT DISTINCT p FROM Poll p JOIN p.participants u WHERE u.email = :email")
    List<Poll> findPollsByParticipantEmail(@Param("email") String email);

    List<Poll> findByEndDateBefore(LocalDateTime date);

    List<Poll> findByCampaign(Campaign campaign);

}