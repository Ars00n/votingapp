package com.example.votingapp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CampaignDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PollDTO> polls;
}
