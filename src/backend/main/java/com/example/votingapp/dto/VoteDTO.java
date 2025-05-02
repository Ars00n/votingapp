package com.example.votingapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoteDTO {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Poll ID cannot be null")
    private Long pollId;

    @NotNull(message = "Candidate ID cannot be null")
    private Long candidateId;  // Замість списку використовуємо одне значення

    @NotNull(message = "User email cannot be null")
    private String userEmail;
}
