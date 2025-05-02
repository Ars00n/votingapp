package com.example.votingapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class PollDTO {
    private Long id;
    private String name;
    private Long campaignId; // Додано поле campaignId
    private List<CandidateDTO> candidates;
    private List<String> participantsEmails; // Додано поле для emails учасників

    // Геттери і сеттери будуть автоматично згенеровані завдяки анотації @Data
}
