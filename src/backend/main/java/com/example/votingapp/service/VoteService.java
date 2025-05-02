package com.example.votingapp.service;

import com.example.votingapp.dto.VoteDTO;
import com.example.votingapp.model.*;
import com.example.votingapp.repository.VoteRepository;
import com.example.votingapp.repository.PollRepository;
import com.example.votingapp.repository.CandidateRepository;
import com.example.votingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Vote save(VoteDTO voteDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Poll poll = pollRepository.findById(voteDTO.getPollId())
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + voteDTO.getPollId()));

        Candidate candidate = candidateRepository.findById(voteDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + voteDTO.getCandidateId()));

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setPoll(poll);
        vote.setCandidate(candidate);
        vote.setCampaign(poll.getCampaign());
        vote.setTimestamp(LocalDateTime.now());

        return voteRepository.save(vote);
    }

    public Optional<Vote> findVoteByUserAndPoll(String userEmail, Long pollId) {
        return voteRepository.findByUserEmailAndPoll_Id(userEmail, pollId);
    }

    public boolean hasUserVotedInPoll(String userEmail, Long pollId) {
        return voteRepository.existsByUserEmailAndPoll_Id(userEmail, pollId);
    }

    public List<Vote> findAll() {
        return voteRepository.findAll();
    }

    public Optional<Vote> findById(Long id) {
        return voteRepository.findById(id);
    }

    public void deleteById(Long id) {
        voteRepository.deleteById(id);
    }

    public List<Vote> findCompletedVotes() {
        LocalDateTime now = LocalDateTime.now();
        List<Poll> completedPolls = pollRepository.findByEndDateBefore(now);
        return voteRepository.findByPollIn(completedPolls);
    }

    public Map<Candidate, Long> getVotingResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        List<Vote> votes = voteRepository.findByPoll(poll);
        return votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidate, Collectors.counting()));
    }
}