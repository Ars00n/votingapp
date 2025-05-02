package com.example.votingapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = {"user", "campaign", "poll", "candidate"})
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-votes")
    private User user;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    @JsonBackReference(value = "campaign-votes")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonBackReference(value = "candidate-votes")
    private Candidate candidate;

    private LocalDateTime timestamp;
}
