package com.example.votingapp.dto;

import java.util.List;

public class PollReportDTO {
    private String pollName;
    private List<CandidateReportDTO> candidates;

    // Конструктор без параметрів
    public PollReportDTO() {
    }

    // Конструктор з параметрами
    public PollReportDTO(String pollName, List<CandidateReportDTO> candidates) {
        this.pollName = pollName;
        this.candidates = candidates;
    }

    // Геттери та сеттери
    public String getPollName() {
        return pollName;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public List<CandidateReportDTO> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateReportDTO> candidates) {
        this.candidates = candidates;
    }
}