package com.example.votingapp.controller;

import com.example.votingapp.dto.PollReportDTO;
import com.example.votingapp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyst")
public class AnalystController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/report/{campaignId}")
    public ResponseEntity<List<PollReportDTO>> generateReport(@PathVariable Long campaignId) {
        List<PollReportDTO> report = reportService.generateReport(campaignId);
        return ResponseEntity.ok(report);
    }
}
