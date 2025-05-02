package com.example.votingapp.service;

import com.example.votingapp.model.Campaign;
import com.example.votingapp.repository.CampaignRepository;
import com.example.votingapp.repository.PollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    @Autowired
    private CampaignRepository campaignRepository;


    public Campaign save(Campaign campaign) {
        logger.info("Saving campaign: {}", campaign.getName());
        if (campaign.getPolls() == null) {
            campaign.setPolls(new ArrayList<>()); // Переконайтеся, що polls не є null
        }
        return campaignRepository.save(campaign);
    }

    public List<Campaign> findAll() {
        logger.debug("Fetching all campaigns");
        return campaignRepository.findAll();
    }

    public Campaign findById(Long id) {
        logger.debug("Finding campaign by ID: {}", id);
        return campaignRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        logger.info("Deleting campaign with ID: {}", id);
        campaignRepository.deleteById(id);
    }

    public List<Campaign> findEndedCampaigns() {
        LocalDate today = LocalDate.now();
        logger.debug("Fetching ended campaigns before: {}", today);
        return campaignRepository.findByEndDateBefore(today);
    }

    public List<Campaign> getActiveCampaigns() {
        LocalDate today = LocalDate.now();
        logger.debug("Fetching active campaigns after: {}", today);
        List<Campaign> campaigns = campaignRepository.findByEndDateAfter(today);
        logger.info("Found {} active campaigns", campaigns.size());
        return campaigns;
    }

    @Transactional(readOnly = true)
    public List<Campaign> getActiveCampaignsForUser(String email) {
        logger.debug("Fetching active campaigns for user: {}", email);
        List<Campaign> campaigns = campaignRepository.findActiveCampaignsByUserEmail(email);
        logger.info("Found {} active campaigns for user {}", campaigns.size(), email);
        return campaigns;
    }
}