package com.example.votingapp.controller;

import com.example.votingapp.dto.CandidateDTO;
import com.example.votingapp.dto.PollDTO;
import com.example.votingapp.dto.CampaignDTO;
import com.example.votingapp.model.Campaign;
import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.Poll;
import com.example.votingapp.model.User;
import com.example.votingapp.repository.CampaignRepository;
import com.example.votingapp.repository.PollRepository;
import com.example.votingapp.repository.UserRepository;
import com.example.votingapp.service.CampaignService;
import com.example.votingapp.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    @PostMapping
    public ResponseEntity<CampaignDTO> createCampaign(@RequestBody Campaign campaign) {
        try {
            Campaign createdCampaign = campaignService.save(campaign);
            CampaignDTO campaignDTO = convertToDTO(createdCampaign);
            return new ResponseEntity<>(campaignDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CampaignDTO>> getAllCampaigns() {
        try {
            List<Campaign> campaigns = campaignService.findAll();
            List<CampaignDTO> campaignDTOs = campaigns.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(campaignDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignDTO> getCampaignById(@PathVariable Long id) {
        try {
            Campaign campaign = campaignService.findById(id);
            return new ResponseEntity<>(convertToDTO(campaign), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        try {
            campaignService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ended")
    public ResponseEntity<List<CampaignDTO>> getEndedCampaigns() {
        try {
            List<Campaign> endedCampaigns = campaignService.findEndedCampaigns();
            List<CampaignDTO> campaignDTOs = endedCampaigns.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(campaignDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<CampaignDTO>> getActiveCampaigns() {
        try {
            List<Campaign> activeCampaigns = campaignService.getActiveCampaigns();
            List<CampaignDTO> campaignDTOs = activeCampaigns.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(campaignDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{campaignId}/polls")
    public ResponseEntity<PollDTO> createPoll(@PathVariable Long campaignId, @RequestBody PollDTO pollDTO) {
        try {
            pollDTO.setCampaignId(campaignId);
            Poll createdPoll = pollService.createPoll(pollDTO);
            return new ResponseEntity<>(convertToPollDTO(createdPoll), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{campaignId}/polls")
    public ResponseEntity<List<PollDTO>> getPollsByCampaignId(@PathVariable Long campaignId) {
        try {
            List<Poll> polls = pollRepository.findByCampaign_Id(campaignId);
            List<PollDTO> pollDTOs = polls.stream()
                    .map(this::convertToPollDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(pollDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/active")
    public ResponseEntity<List<CampaignDTO>> getActiveCampaignsByUserEmail(@RequestParam String email) {
        try {
            List<Campaign> campaigns = campaignService.getActiveCampaignsForUser(email);
            List<CampaignDTO> campaignDTOs = campaigns.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(campaignDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Конвертація в DTO для уникнення рекурсії
    private CampaignDTO convertToDTO(Campaign campaign) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(campaign.getId());
        dto.setName(campaign.getName());
        dto.setStartDate(campaign.getStartDate());
        dto.setEndDate(campaign.getEndDate());
        dto.setPolls(campaign.getPolls().stream()
                .map(this::convertToPollDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private PollDTO convertToPollDTO(Poll poll) {
        PollDTO dto = new PollDTO();
        dto.setId(poll.getId());
        dto.setName(poll.getName());
        dto.setCampaignId(poll.getCampaign().getId());
        dto.setCandidates(poll.getCandidates().stream()
                .map(this::convertToCandidateDTO)
                .collect(Collectors.toList()));
        dto.setParticipantsEmails(poll.getParticipants().stream()
                .map(User::getEmail)
                .collect(Collectors.toList()));
        return dto;
    }

    private CandidateDTO convertToCandidateDTO(Candidate candidate) {
        CandidateDTO dto = new CandidateDTO();
        dto.setId(candidate.getId());
        dto.setFullName(candidate.getFullName());
        return dto;
    }
}
