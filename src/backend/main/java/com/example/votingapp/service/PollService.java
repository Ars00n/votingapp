package com.example.votingapp.service;

import com.example.votingapp.dto.CandidateDTO;
import com.example.votingapp.dto.PollDTO;
import com.example.votingapp.exception.PollNotFoundException;
import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.Poll;
import com.example.votingapp.model.User;
import com.example.votingapp.model.Campaign;
import com.example.votingapp.repository.CandidateRepository;
import com.example.votingapp.repository.PollRepository;
import com.example.votingapp.repository.UserRepository;
import com.example.votingapp.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {

    private static final Logger logger = LoggerFactory.getLogger(PollService.class);

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final CandidateRepository candidateRepository;

    public PollService(PollRepository pollRepository,
                       UserRepository userRepository,
                       CampaignRepository campaignRepository,
                       CandidateRepository candidateRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.candidateRepository = candidateRepository;
    }

    public List<Poll> getPollsByCampaign(Long campaignId) {
        return pollRepository.findByCampaign_Id(campaignId);
    }

    public Poll addCandidateToPoll(Long pollId, CandidateDTO candidateDTO) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Невірний ID опитування"));

        // Перевірка, чи вже існує кандидат
        boolean candidateExists = poll.getCandidates().stream()
                .anyMatch(c -> c.getFullName().equals(candidateDTO.getFullName()));

        if (candidateExists) {
            throw new IllegalArgumentException("Кандидат з таким іменем вже існує в опитуванні");
        }

        Candidate candidate = new Candidate();
        candidate.setFullName(candidateDTO.getFullName());
        candidate.setPoll(poll);

        poll.getCandidates().add(candidate);
        return pollRepository.save(poll);
    }

    public Poll addParticipantToPoll(Long pollId, String email) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Невірний ID опитування"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));
        if (!poll.getParticipants().contains(user)) {
            poll.getParticipants().add(user);
        }
        return pollRepository.save(poll);
    }

    @Transactional
    public Poll createPoll(PollDTO pollDTO) {
        Campaign campaign = campaignRepository.findById(pollDTO.getCampaignId())
                .orElseThrow(() -> new IllegalArgumentException("Кампанію не знайдено"));

        List<User> users = pollDTO.getParticipantsEmails().stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено: " + email)))
                .collect(Collectors.toList());

        Poll poll = new Poll();
        poll.setName(pollDTO.getName());
        poll.setParticipants(users);
        poll.setCampaign(campaign);

        List<Candidate> candidates = pollDTO.getCandidates().stream()
                .map(candidateDTO -> {
                    Candidate candidate = new Candidate();
                    candidate.setFullName(candidateDTO.getFullName());
                    candidate.setPoll(poll);
                    return candidate;
                })
                .collect(Collectors.toList());

        poll.setCandidates(candidates);

        return pollRepository.save(poll);
    }

    public List<String> getParticipantEmailsForPoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Невірний ID опитування"));

        return poll.getParticipants().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    public PollDTO getPollById(Long id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new PollNotFoundException("Опитування не знайдено з ID: " + id));

        PollDTO pollDTO = new PollDTO();
        pollDTO.setId(poll.getId());
        pollDTO.setCampaignId(poll.getCampaign().getId());
        pollDTO.setName(poll.getName());
        pollDTO.setParticipantsEmails(poll.getParticipants().stream()
                .map(User::getEmail)
                .collect(Collectors.toList()));
        pollDTO.setCandidates(poll.getCandidates().stream()
                .map(candidate -> new CandidateDTO(candidate.getId(), candidate.getFullName()))
                .collect(Collectors.toList()));

        return pollDTO;
    }

    @Transactional
    public Poll updatePoll(PollDTO pollDTO) {
        Poll existingPoll = pollRepository.findById(pollDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Опитування не знайдено"));

        existingPoll.setName(pollDTO.getName());

        // Оновлення кандидатів
        List<Candidate> updatedCandidates = new ArrayList<>();
        for (CandidateDTO candidateDTO : pollDTO.getCandidates()) {
            if (candidateDTO.getId() != null) {
                Candidate existingCandidate = existingPoll.getCandidates().stream()
                        .filter(c -> c.getId().equals(candidateDTO.getId()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Кандидата не знайдено"));
                existingCandidate.setFullName(candidateDTO.getFullName());
                updatedCandidates.add(existingCandidate);
            } else {
                Candidate newCandidate = new Candidate();
                newCandidate.setFullName(candidateDTO.getFullName());
                newCandidate.setPoll(existingPoll);
                updatedCandidates.add(newCandidate);
            }
        }

        existingPoll.getCandidates().clear();
        existingPoll.getCandidates().addAll(updatedCandidates);

        // Оновлення учасників
        List<User> updatedParticipants = new ArrayList<>();
        for (String email : pollDTO.getParticipantsEmails()) {
            User participant = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Користувача не знайдено"));
            updatedParticipants.add(participant);
        }

        existingPoll.getParticipants().clear();
        existingPoll.getParticipants().addAll(updatedParticipants);

        return pollRepository.save(existingPoll);
    }

    public void deletePoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new PollNotFoundException("Опитування не знайдено з ID: " + pollId));

        pollRepository.delete(poll);
    }

    // Утилітарний метод для конвертації Poll в PollDTO
    private PollDTO convertPollToDTO(Poll poll) {
        PollDTO pollDTO = new PollDTO();
        pollDTO.setId(poll.getId());
        pollDTO.setCampaignId(poll.getCampaign().getId());
        pollDTO.setName(poll.getName());
        pollDTO.setParticipantsEmails(poll.getParticipants().stream()
                .map(User::getEmail)
                .collect(Collectors.toList()));
        pollDTO.setCandidates(poll.getCandidates().stream()
                .map(candidate -> new CandidateDTO(candidate.getId(), candidate.getFullName()))
                .collect(Collectors.toList()));
        return pollDTO;
    }

    public Poll removeCandidateFromPoll(Long pollId, CandidateDTO candidateDTO) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Опитування не знайдено"));

        // Знайти кандидата для видалення
        Candidate candidateToRemove = poll.getCandidates().stream()
                .filter(candidate -> candidate.getFullName().equals(candidateDTO.getFullName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Кандидата не знайдено"));

        // Видалити кандидата з опитування
        poll.getCandidates().remove(candidateToRemove);

        // Зберегти зміни в базі даних
        return pollRepository.save(poll);
    }

    public List<Poll> getPollsForUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return pollRepository.findByParticipantsContains(user);
        } else {
            return Collections.emptyList(); // Повертає порожній список, якщо користувача не знайдено
        }
    }

    public List<Poll> getPollsForUserInCampaign(Long campaignId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return pollRepository.findByCampaign_Id(campaignId).stream()
                .filter(poll -> poll.getParticipants().contains(user))
                .collect(Collectors.toList());
    }
}