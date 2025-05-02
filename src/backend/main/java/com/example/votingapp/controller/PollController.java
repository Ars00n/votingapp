package com.example.votingapp.controller;

import com.example.votingapp.dto.CandidateDTO;
import com.example.votingapp.dto.PollDTO;
import com.example.votingapp.model.Poll;
import com.example.votingapp.service.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private static final Logger logger = LoggerFactory.getLogger(PollController.class);

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @PostMapping("/{campaignId}/polls")
    public ResponseEntity<Poll> createPoll(@PathVariable Long campaignId, @RequestBody PollDTO pollDTO) {
        try {
            pollDTO.setCampaignId(campaignId);
            Poll createdPoll = pollService.createPoll(pollDTO);
            return ResponseEntity.ok(createdPoll);
        } catch (IllegalArgumentException e) {
            logger.error("Business error when creating poll: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<Poll>> getPollsByCampaign(@PathVariable Long campaignId) {
        try {
            List<Poll> polls = pollService.getPollsByCampaign(campaignId);
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            logger.error("Error fetching polls for campaign {}: {}", campaignId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{pollId}/add-candidate")
    public ResponseEntity<Poll> addCandidateToPoll(@PathVariable Long pollId, @RequestBody CandidateDTO candidateDTO) {
        try {
            Poll updatedPoll = pollService.addCandidateToPoll(pollId, candidateDTO);
            if (updatedPoll != null) {
                return ResponseEntity.ok(updatedPoll);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for adding candidate to poll {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Error adding candidate to poll {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/{pollId}/add-participant")
    public ResponseEntity<Poll> addParticipantToPoll(@PathVariable Long pollId, @RequestBody String email) {
        try {
            Poll updatedPoll = pollService.addParticipantToPoll(pollId, email);
            if (updatedPoll != null) {
                return ResponseEntity.ok(updatedPoll);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Error adding participant to poll {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{pollId}/participants-emails")
    public ResponseEntity<List<String>> getParticipantEmails(@PathVariable Long pollId) {
        try {
            List<String> participantEmails = pollService.getParticipantEmailsForPoll(pollId);
            return ResponseEntity.ok(participantEmails);
        } catch (Exception e) {
            logger.error("Error fetching participant emails for poll {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{pollId}")
    public ResponseEntity<PollDTO> getPollById(@PathVariable Long pollId) {
        try {
            PollDTO pollDTO = pollService.getPollById(pollId);
            if (pollDTO != null) {
                return ResponseEntity.ok(pollDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching poll by id {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable Long id, @RequestBody PollDTO pollDTO) {
        pollDTO.setId(id); // Ensure the ID in the DTO matches the path variable
        Poll updatedPoll = pollService.updatePoll(pollDTO);
        return ResponseEntity.ok(updatedPoll);
    }

    @DeleteMapping("/{pollId}")
    public ResponseEntity<Void> deletePoll(@PathVariable Long pollId) {
        try {
            pollService.deletePoll(pollId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting poll with id {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{pollId}/remove-candidate")
    public ResponseEntity<Poll> removeCandidateFromPoll(@PathVariable Long pollId, @RequestBody CandidateDTO candidateDTO) {
        try {
            Poll updatedPoll = pollService.removeCandidateFromPoll(pollId, candidateDTO);
            if (updatedPoll != null) {
                return ResponseEntity.ok(updatedPoll);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Error removing candidate from poll {}: {}", pollId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/campaign/{campaignId}/user")
    public ResponseEntity<List<Poll>> getPollsForUserInCampaign(
            @PathVariable Long campaignId,
            @RequestParam String email) {
        try {
            List<Poll> polls = pollService.getPollsForUserInCampaign(campaignId, email);
            return ResponseEntity.ok(polls);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Error fetching polls for user in campaign: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}