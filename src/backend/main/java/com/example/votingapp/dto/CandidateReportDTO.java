package com.example.votingapp.dto;

public class CandidateReportDTO {
    private String name;
    private int votes;

    public CandidateReportDTO() {
    }

    public CandidateReportDTO(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}