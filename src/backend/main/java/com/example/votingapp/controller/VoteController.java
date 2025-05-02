package com.example.votingapp.controller;

import com.example.votingapp.dto.VoteDTO;
import com.example.votingapp.model.Vote;
import com.example.votingapp.security.JwtTokenProvider;
import com.example.votingapp.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService voteService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public VoteController(VoteService voteService, JwtTokenProvider jwtTokenProvider) {
        this.voteService = voteService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkVote(
            @RequestParam Long pollId,
            @RequestParam String userEmail,
            @RequestHeader("Authorization") String token) {
        try {
            String tokenEmail = jwtTokenProvider.getEmailFromToken(token.substring(7));
            if (!userEmail.equals(tokenEmail)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<Vote> vote = voteService.findVoteByUserAndPoll(userEmail, pollId);
            Map<String, Object> response = new HashMap<>();

            if (vote.isPresent()) {
                response.put("hasVoted", true);
                response.put("candidateId", vote.get().getCandidate().getId());
            } else {
                response.put("hasVoted", false);
                response.put("candidateId", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> castVote(@RequestBody VoteDTO voteDTO, @RequestHeader("Authorization") String token) {
        try {
            String userEmail = jwtTokenProvider.getEmailFromToken(token.substring(7));
            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неавторизований користувач.");
            }

            boolean hasVoted = voteService.hasUserVotedInPoll(userEmail, voteDTO.getPollId());
            if (hasVoted) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ви вже проголосували в цьому опитуванні.");
            }

            Vote savedVote = voteService.save(voteDTO, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body("Голос успішно зараховано.");
        } catch (RuntimeException e) {
            System.out.println("Error in castVote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Сталася помилка під час голосування.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAllVotes() {
        List<Vote> votes = voteService.findAll();
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVoteById(@PathVariable Long id) {
        return ResponseEntity.of(voteService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        voteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
