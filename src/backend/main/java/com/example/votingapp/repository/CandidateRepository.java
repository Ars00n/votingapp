package com.example.votingapp.repository;

import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {


    @Query("SELECT c FROM Candidate c WHERE c.poll.id = :pollId")
    List<Candidate> findCandidatesByPollId(@Param("pollId") Long pollId);

}
