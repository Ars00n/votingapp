package com.example.votingapp.repository;

import com.example.votingapp.model.Vote;
import com.example.votingapp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v JOIN v.campaign c WHERE c.endDate < CURRENT_TIMESTAMP")
    List<Vote> findCompletedVotes();

    List<Vote> findByPollEndDateBefore(LocalDateTime date);
    List<Vote> findByPollIn(List<Poll> polls);
    List<Vote> findByPoll(Poll poll);
    boolean existsByUserEmailAndPoll_Id(String userEmail, Long pollId);
    Optional<Vote> findByUserEmailAndPoll_Id(String userEmail, Long pollId);
}
