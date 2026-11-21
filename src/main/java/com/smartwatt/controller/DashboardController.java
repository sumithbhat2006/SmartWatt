package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.dto.DashboardSummaryDTO;
import com.smartwatt.dto.RoomSummaryDTO;
import com.smartwatt.entity.Alert;
import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.Recommendation;
import com.smartwatt.repository.AlertRepository;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.RecommendationRepository;
import com.smartwatt.service.AlertService;
import com.smartwatt.service.EnergyCalculationService;
import com.smartwatt.service.RoomService;
import com.smartwatt.service.SettingsService;
import com.smartwatt.simulation.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RoomService roomService;
    private final ApplianceRepository applianceRepository;
    private final AlertService alertService;
    private final RecommendationRepository recommendationRepository;
    private final SettingsService settingsService;
    private final SimulationService simulationService;

    public DashboardController(RoomService roomService,
                               ApplianceRepository applianceRepository,
                               AlertService alertService,
                               RecommendationRepository recommendationRepository,
                               SettingsService settingsService,
                               SimulationService simulationService) {
        this.roomService = roomService;
        this.applianceRepository = applianceRepository;
        this.alertService = alertService;
        this.recommendationRepository = recommendationRepository;
        this.settingsService = settingsService;
        this.simulationService = simulationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        List<RoomSummaryDTO> rooms = roomService.getRoomSummaries();
        List<Appliance> allAppliances = applianceRepository.findAll();
        Double tariff = settingsService.getTariffRate();
        Double maxPowerThreshold = settingsService.getMaxPowerThreshold();

        double totalPower = 0.0;
        double totalKwh = 0.0;
        int activeCount = 0;

        for (Appliance app : allAppliances) {
            if (Boolean.TRUE.equals(app.getIsOn())) {
                totalPower += (app.getCurrentWattage() != null ? app.getCurrentWattage() : app.getBaseWattage());
                activeCount++;
            }
            if (app.getTotalEnergyKwh() != null) {
                totalKwh += app.getTotalEnergyKwh();
            }
        }

        double estimatedBill = Math.round(totalKwh * tariff * 100.0) / 100.0;
        double gridVoltage = simulationService.getCurrentGridVoltage();
        double totalCurrent = gridVoltage > 0 ? Math.round((totalPower / gridVoltage) * 100.0) / 100.0 : 0.0;

        // Determine system status
        String systemStatus;
        if (totalPower >= maxPowerThreshold) {
            systemStatus = "CRITICAL";
        } else if (totalPower >= (maxPowerThreshold * 0.70)) {
            systemStatus = "HIGH_CONSUMPTION";
        } else if (activeCount >= 4) {
            systemStatus = "MODERATE";
        } else {
            systemStatus = "NORMAL";
        }

        List<Alert> activeAlerts = alertService.getActiveAlerts();
        List<Recommendation> activeRecs = recommendationRepository.findByIsActiveTrueOrderByCreatedAtDesc();

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setTotalPowerWatts(Math.round(totalPower * 10.0) / 10.0);
        summary.setTotalEnergyKwh(Math.round(totalKwh * 1000.0) / 1000.0);
        summary.setEstimatedBillInr(estimatedBill);
        summary.setActiveAppliancesCount(activeCount);
        summary.setTotalAppliancesCount(allAppliances.size());
        summary.setTariffRatePerKwh(tariff);
        summary.setPowerThresholdWatts(maxPowerThreshold);
        summary.setSystemStatus(systemStatus);
        summary.setIsSimulationActive(settingsService.isSimulationEnabled());
        summary.setSimulatedGridVoltage(gridVoltage);
        summary.setTotalCurrentAmp(totalCurrent);
        summary.setRooms(rooms);
        summary.setRecentAlerts(activeAlerts);
        summary.setActiveRecommendations(activeRecs);

        return ResponseEntity.ok(ApiResponse.ok("Dashboard data retrieved successfully", summary));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomSummaryDTO>>> getRooms() {
        List<RoomSummaryDTO> rooms = roomService.getRoomSummaries();
        return ResponseEntity.ok(ApiResponse.ok("Rooms retrieved successfully", rooms));
    }
}
