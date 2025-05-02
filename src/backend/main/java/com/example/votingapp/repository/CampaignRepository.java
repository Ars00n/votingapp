package com.example.votingapp.repository;

import com.example.votingapp.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    /**
     * Знаходить кампанії, які закінчилися до вказаної дати
     */
    List<Campaign> findByEndDateBefore(LocalDate endDate);

    /**
     * Знаходить кампанії без опитувань
     */
    List<Campaign> findByPollsIsNull();

    /**
     * Знаходить активні кампанії (які ще не закінчились)
     */
    @Query("SELECT c FROM Campaign c WHERE c.endDate > CURRENT_DATE")
    List<Campaign> findByEndDateAfter(LocalDate currentDate);

    /**
     * Знаходить активні кампанії для конкретного користувача
     */
    @Query("SELECT DISTINCT c FROM Campaign c " +
            "JOIN c.polls p " +
            "JOIN p.participants u " +
            "WHERE u.email = :email " +
            "AND c.endDate > CURRENT_DATE " +
            "ORDER BY c.endDate ASC")
    List<Campaign> findActiveCampaignsByUserEmail(@Param("email") String email);
}