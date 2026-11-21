package com.smartwatt.service;

import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.Recommendation;
import com.smartwatt.entity.Room;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.RecommendationRepository;
import com.smartwatt.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ApplianceRepository applianceRepository;
    private final RoomRepository roomRepository;
    private final SettingsService settingsService;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 ApplianceRepository applianceRepository,
                                 RoomRepository roomRepository,
                                 SettingsService settingsService) {
        this.recommendationRepository = recommendationRepository;
        this.applianceRepository = applianceRepository;
        this.roomRepository = roomRepository;
        this.settingsService = settingsService;
    }

    public List<Recommendation> getActiveRecommendations() {
        return recommendationRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public void generateIntelligentRecommendations() {
        List<Appliance> allAppliances = applianceRepository.findAll();
        List<Room> allRooms = roomRepository.findAll();
        Double tariff = settingsService.getTariffRate();

        List<Recommendation> generated = new ArrayList<>();

        // Rule 1: High power appliance left ON (e.g., AC or Heater > 1000W)
        for (Appliance app : allAppliances) {
            if (Boolean.TRUE.equals(app.getIsOn()) && app.getBaseWattage() >= 1000.0) {
                // If run for 2 hours less daily, calculate savings
                double dailySavingsKwh = (app.getBaseWattage() * 2.0) / 1000.0;
                double monthlySavingsInr = dailySavingsKwh * 30.0 * tariff;

                generated.add(new Recommendation(
                        "High Load Optimization: " + app.getName(),
                        String.format("%s in %s is consuming %.0f W. Setting temperature to 24°C or reducing usage by 2 hours daily saves significant electricity.",
                                app.getName(), app.getRoom().getName(), app.getCurrentWattage()),
                        "HIGH_WATTAGE",
                        Math.round(monthlySavingsInr * 10.0) / 10.0,
                        "Enable Eco-Mode or set timer on " + app.getName()
                ));
            }
        }

        // Rule 2: Long running fan or light (> 3 hours continuous)
        for (Appliance app : allAppliances) {
            if (Boolean.TRUE.equals(app.getIsOn()) && app.getActiveDurationHours() != null && app.getActiveDurationHours() >= 3.0) {
                double monthlySavings = (app.getBaseWattage() * app.getActiveDurationHours() * 30.0 / 1000.0) * tariff;
                generated.add(new Recommendation(
                        "Extended Usage Detected: " + app.getName(),
                        String.format("%s has been running continuously for %.1f hours in %s. Switch off if the room is unoccupied.",
                                app.getName(), app.getActiveDurationHours(), app.getRoom().getName()),
                        "LONG_RUNNING",
                        Math.round(monthlySavings * 10.0) / 10.0,
                        "Turn off " + app.getName() + " when not in room"
                ));
            }
        }

        // Rule 3: Room imbalance (One room accounts for > 60% of total active wattage)
        double totalWattage = allAppliances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsOn()))
                .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : 0.0)
                .sum();

        if (totalWattage > 100) {
            for (Room room : allRooms) {
                double roomWattage = allAppliances.stream()
                        .filter(a -> a.getRoom().getId().equals(room.getId()) && Boolean.TRUE.equals(a.getIsOn()))
                        .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : 0.0)
                        .sum();

                double roomRatio = roomWattage / totalWattage;
                if (roomRatio >= 0.60) {
                    double monthlySavings = (roomWattage * 0.25 * 6.0 * 30.0 / 1000.0) * tariff;
                    generated.add(new Recommendation(
                            "Room Imbalance: " + room.getName(),
                            String.format("%s accounts for %.0f%% of total building power consumption (%.0f W of %.0f W). Inspect active loads in this room.",
                                    room.getName(), roomRatio * 100, roomWattage, totalWattage),
                            "ROOM_BALANCE",
                            Math.round(monthlySavings * 10.0) / 10.0,
                            "Audit active appliances in " + room.getName()
                    ));
                }
            }
        }

        // Rule 4: Always provide a standard energy efficiency tip if list is small
        if (generated.isEmpty()) {
            generated.add(new Recommendation(
                    "LED & Star-Rated Appliance Efficiency",
                    "Replacing conventional lights with LED fixtures and utilizing BLDC ceiling fans can reduce room lighting & ventilation energy usage by up to 55%.",
                    "SAVING_TIP",
                    450.0,
                    "Consider BLDC fans and 5-star rated equipment"
            ));
        }

        // Save active recommendations, avoiding exact duplicates
        for (Recommendation rec : generated) {
            boolean exists = recommendationRepository.findByIsActiveTrueOrderByCreatedAtDesc().stream()
                    .anyMatch(r -> r.getTitle().equalsIgnoreCase(rec.getTitle()));
            if (!exists) {
                recommendationRepository.save(rec);
            }
        }
    }

    @Transactional
    public void dismissRecommendation(Long id) {
        recommendationRepository.findById(id).ifPresent(rec -> {
            rec.setIsActive(false);
            recommendationRepository.save(rec);
        });
    }
}
