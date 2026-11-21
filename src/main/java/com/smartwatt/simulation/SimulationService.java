package com.smartwatt.simulation;

import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.EnergyUsage;
import com.smartwatt.entity.Reading;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.EnergyUsageRepository;
import com.smartwatt.repository.ReadingRepository;
import com.smartwatt.service.AlertService;
import com.smartwatt.service.RecommendationService;
import com.smartwatt.service.SettingsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Stage 2 Functional Prototype Simulation Engine.
 * Simulates realistic electrical sensor telemetry (Voltage, Current, Power)
 * for ESP32 hardware drop-in compatibility in future stages.
 */
@Service
public class SimulationService {

    private final ApplianceRepository applianceRepository;
    private final ReadingRepository readingRepository;
    private final EnergyUsageRepository energyUsageRepository;
    private final SettingsService settingsService;
    private final AlertService alertService;
    private final RecommendationService recommendationService;

    private final Random random = new Random();
    private double currentGridVoltage = 230.0;
    private long tickCounter = 0;

    @Value("${smartwatt.simulation.interval-seconds:3}")
    private int intervalSeconds;

    public SimulationService(ApplianceRepository applianceRepository,
                             ReadingRepository readingRepository,
                             EnergyUsageRepository energyUsageRepository,
                             SettingsService settingsService,
                             AlertService alertService,
                             RecommendationService recommendationService) {
        this.applianceRepository = applianceRepository;
        this.readingRepository = readingRepository;
        this.energyUsageRepository = energyUsageRepository;
        this.settingsService = settingsService;
        this.alertService = alertService;
        this.recommendationService = recommendationService;
    }

    public double getCurrentGridVoltage() {
        return currentGridVoltage;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void runSimulationTick() {
        if (!settingsService.isSimulationEnabled()) {
            return;
        }

        tickCounter++;

        // Simulate realistic grid voltage fluctuation (227V - 233V AC)
        double voltageJitter = (random.nextDouble() * 4.0) - 2.0; // -2.0 to +2.0
        currentGridVoltage = Math.round((230.0 + voltageJitter) * 10.0) / 10.0;

        List<Appliance> appliances = applianceRepository.findAll();
        double totalPower = 0.0;
        LocalDate today = LocalDate.now();

        double hoursElapsed = intervalSeconds / 3600.0;

        for (Appliance app : appliances) {
            if (Boolean.TRUE.equals(app.getIsOn())) {
                // Realistic load fluctuation: +/- 2%
                double variation = 1.0 + ((random.nextDouble() * 0.04) - 0.02);
                double simulatedWatts = Math.round((app.getBaseWattage() * variation) * 10.0) / 10.0;
                double simulatedAmps = Math.round((simulatedWatts / currentGridVoltage) * 100.0) / 100.0;

                app.setCurrentWattage(simulatedWatts);
                app.setCurrentVoltage(currentGridVoltage);
                app.setCurrentAmp(simulatedAmps);

                // Accumulate active duration & kWh
                double currentHours = app.getActiveDurationHours() != null ? app.getActiveDurationHours() : 0.0;
                app.setActiveDurationHours(currentHours + hoursElapsed);

                // Energy (kWh) = Power (W) * Time (hours) / 1000
                double deltaKwh = (simulatedWatts * hoursElapsed) / 1000.0;
                double currentKwh = app.getTotalEnergyKwh() != null ? app.getTotalEnergyKwh() : 0.0;
                app.setTotalEnergyKwh(currentKwh + deltaKwh);

                totalPower += simulatedWatts;

                // Update EnergyUsage table for today
                updateDailyEnergyUsage(app, today, hoursElapsed, deltaKwh);

                // Save sensor reading sample (every 3 ticks = ~9-10 seconds to keep DB lean)
                if (tickCounter % 3 == 0) {
                    Reading reading = new Reading(app, currentGridVoltage, simulatedAmps, simulatedWatts, true, true);
                    readingRepository.save(reading);
                }

                // Check long-running alerts
                alertService.evaluateApplianceDuration(app);
            } else {
                app.setCurrentWattage(0.0);
                app.setCurrentVoltage(currentGridVoltage);
                app.setCurrentAmp(0.0);
            }
        }

        applianceRepository.saveAll(appliances);

        // Check overall total power threshold alerts
        alertService.evaluateTotalPower(totalPower);

        // Periodically run recommendation engine (every 5 ticks)
        if (tickCounter % 5 == 0) {
            recommendationService.generateIntelligentRecommendations();
        }
    }

    private void updateDailyEnergyUsage(Appliance app, LocalDate today, double hoursElapsed, double deltaKwh) {
        EnergyUsage usage = energyUsageRepository.findByApplianceIdAndUsageDate(app.getId(), today)
                .orElse(new EnergyUsage(app.getRoom(), app, today, 0.0, 0.0, 0.0));

        double updatedHours = usage.getActiveHours() + hoursElapsed;
        double updatedKwh = usage.getTotalKwh() + deltaKwh;
        double tariff = settingsService.getTariffRate();

        usage.setActiveHours(Math.round(updatedHours * 1000.0) / 1000.0);
        usage.setTotalKwh(Math.round(updatedKwh * 10000.0) / 10000.0);
        usage.setCalculatedCost(Math.round(updatedKwh * tariff * 100.0) / 100.0);
        usage.setUpdatedAt(LocalDateTime.now());

        energyUsageRepository.save(usage);
    }
}
