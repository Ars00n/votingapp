package com.example.votingapp.service;

import com.example.votingapp.dto.CandidateReportDTO;
import com.example.votingapp.dto.PollReportDTO;
import com.example.votingapp.model.Campaign;
import com.example.votingapp.model.Poll;
import com.example.votingapp.repository.CampaignRepository;
import com.example.votingapp.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    public List<PollReportDTO> generateReport(Long campaignId) {
        // Отримуємо Campaign за його id
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        // Викликаємо findByCampaign
        List<Poll> polls = pollRepository.findByCampaign(campaign);
        List<PollReportDTO> report = new ArrayList<>();

        for (Poll poll : polls) {
            PollReportDTO pollReportDTO = new PollReportDTO();
            pollReportDTO.setPollName(poll.getName());

            List<CandidateReportDTO> candidatesReport = poll.getCandidates().stream()
                    .map(candidate -> new CandidateReportDTO(candidate.getFullName(), candidate.getVotes().size()))
                    .collect(Collectors.toList());

            pollReportDTO.setCandidates(candidatesReport);
            report.add(pollReportDTO);
        }

        return report;
    }
}
