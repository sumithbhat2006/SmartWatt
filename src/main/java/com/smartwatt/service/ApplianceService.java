package com.smartwatt.service;

import com.smartwatt.dto.ApplianceDTO;
import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.Reading;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.ReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplianceService {

    private final ApplianceRepository applianceRepository;
    private final ReadingRepository readingRepository;
    private final AlertService alertService;
    private final RecommendationService recommendationService;

    public ApplianceService(ApplianceRepository applianceRepository,
                            ReadingRepository readingRepository,
                            AlertService alertService,
                            RecommendationService recommendationService) {
        this.applianceRepository = applianceRepository;
        this.readingRepository = readingRepository;
        this.alertService = alertService;
        this.recommendationService = recommendationService;
    }

    public List<ApplianceDTO> getAllAppliances() {
        return applianceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ApplianceDTO> getAppliancesByRoom(Long roomId) {
        return applianceRepository.findByRoomId(roomId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplianceDTO toggleAppliance(Long applianceId, Boolean requestedState) {
        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new IllegalArgumentException("Appliance not found with ID: " + applianceId));

        boolean newState = requestedState != null ? requestedState : !Boolean.TRUE.equals(appliance.getIsOn());
        appliance.setIsOn(newState);
        appliance.setLastStatusChange(LocalDateTime.now());

        double voltage = 230.0;
        double wattage;
        double currentAmp;

        if (newState) {
            wattage = appliance.getBaseWattage();
            currentAmp = Math.round((wattage / voltage) * 100.0) / 100.0;
            appliance.setCurrentWattage(wattage);
            appliance.setCurrentVoltage(voltage);
            appliance.setCurrentAmp(currentAmp);
        } else {
            wattage = 0.0;
            currentAmp = 0.0;
            appliance.setCurrentWattage(0.0);
            appliance.setCurrentVoltage(voltage);
            appliance.setCurrentAmp(0.0);
        }

        Appliance saved = applianceRepository.save(appliance);

        // Record immediate reading in readings table
        Reading reading = new Reading(saved, voltage, currentAmp, wattage, newState, true);
        readingRepository.save(reading);

        // Run checks
        double totalActivePower = applianceRepository.findByIsOnTrue().stream()
                .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : a.getBaseWattage())
                .sum();
        alertService.evaluateTotalPower(totalActivePower);

        double roomActivePower = applianceRepository.findByRoomId(saved.getRoom().getId()).stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsOn()))
                .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : a.getBaseWattage())
                .sum();
        alertService.evaluateRoomPower(saved.getRoom(), roomActivePower);

        recommendationService.generateIntelligentRecommendations();

        return mapToDTO(saved);
    }

    public ApplianceDTO mapToDTO(Appliance app) {
        ApplianceDTO dto = new ApplianceDTO();
        dto.setId(app.getId());
        dto.setRoomId(app.getRoom().getId());
        dto.setRoomName(app.getRoom().getName());
        dto.setName(app.getName());
        dto.setType(app.getType());
        dto.setIcon(app.getIcon());
        dto.setBaseWattage(app.getBaseWattage());
        dto.setIsOn(app.getIsOn());
        dto.setCurrentWattage(app.getCurrentWattage() != null ? Math.round(app.getCurrentWattage() * 10.0) / 10.0 : 0.0);
        dto.setCurrentVoltage(app.getCurrentVoltage() != null ? Math.round(app.getCurrentVoltage() * 10.0) / 10.0 : 230.0);
        dto.setCurrentAmp(app.getCurrentAmp() != null ? Math.round(app.getCurrentAmp() * 100.0) / 100.0 : 0.0);
        dto.setActiveDurationHours(app.getActiveDurationHours() != null ? Math.round(app.getActiveDurationHours() * 10.0) / 10.0 : 0.0);
        dto.setTotalEnergyKwh(app.getTotalEnergyKwh() != null ? Math.round(app.getTotalEnergyKwh() * 1000.0) / 1000.0 : 0.0);
        dto.setLastStatusChange(app.getLastStatusChange());
        return dto;
    }
}
