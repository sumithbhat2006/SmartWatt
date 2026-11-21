package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.dto.EnergyAnalyticsDTO;
import com.smartwatt.service.EnergyCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final EnergyCalculationService energyCalculationService;

    public AnalyticsController(EnergyCalculationService energyCalculationService) {
        this.energyCalculationService = energyCalculationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<EnergyAnalyticsDTO>> getAnalytics() {
        EnergyAnalyticsDTO analytics = energyCalculationService.getAnalytics();
        return ResponseEntity.ok(ApiResponse.ok("Analytics data retrieved successfully", analytics));
    }
}
