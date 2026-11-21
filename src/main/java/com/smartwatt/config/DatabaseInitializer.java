package com.smartwatt.config;

import com.smartwatt.entity.*;
import com.smartwatt.repository.*;
import com.smartwatt.service.SettingsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Initializes database with default Room, Appliance, Settings, and initial historical data
 * if the database is empty.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final ApplianceRepository applianceRepository;
    private final EnergyUsageRepository energyUsageRepository;
    private final AlertRepository alertRepository;
    private final RecommendationRepository recommendationRepository;
    private final SettingRepository settingRepository;
    private final SettingsService settingsService;

    public DatabaseInitializer(RoomRepository roomRepository,
                               ApplianceRepository applianceRepository,
                               EnergyUsageRepository energyUsageRepository,
                               AlertRepository alertRepository,
                               RecommendationRepository recommendationRepository,
                               SettingRepository settingRepository,
                               SettingsService settingsService) {
        this.roomRepository = roomRepository;
        this.applianceRepository = applianceRepository;
        this.energyUsageRepository = energyUsageRepository;
        this.alertRepository = alertRepository;
        this.recommendationRepository = recommendationRepository;
        this.settingRepository = settingRepository;
        this.settingsService = settingsService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initializeSettings();

        if (roomRepository.count() == 0) {
            seedRoomsAndAppliances();
            seedHistoricalEnergyData();
            seedInitialAlertsAndRecommendations();
        }
    }

    private void initializeSettings() {
        settingsService.saveOrUpdateSetting(SettingsService.KEY_TARIFF_RATE, "7.50", "Electricity Tariff in INR per kWh");
        settingsService.saveOrUpdateSetting(SettingsService.KEY_MAX_POWER_THRESHOLD, "2500.0", "Total Power Safety Threshold in Watts");
        settingsService.saveOrUpdateSetting(SettingsService.KEY_ROOM_POWER_THRESHOLD, "1800.0", "Per Room Power Threshold in Watts");
        settingsService.saveOrUpdateSetting(SettingsService.KEY_LONG_RUNNING_HOURS, "4.0", "Appliance Continuous Usage Warning Threshold in Hours");
        settingsService.saveOrUpdateSetting(SettingsService.KEY_SIMULATION_ENABLED, "true", "Enable or disable sensor telemetry simulation");
    }

    private void seedRoomsAndAppliances() {
        // Room 1: Living Room
        Room room1 = new Room("Room 1 - Living Room", "ROOM_1", "Primary living area and entertainment zone");
        room1 = roomRepository.save(room1);

        Appliance r1Bulb = new Appliance(room1, "Living Room LED Bulb", "BULB", "lightbulb", 15.0, true);
        r1Bulb.setActiveDurationHours(2.5);
        r1Bulb.setTotalEnergyKwh(0.35);

        Appliance r1Fan = new Appliance(room1, "Ceiling Fan", "FAN", "fan", 75.0, true);
        r1Fan.setActiveDurationHours(4.2);
        r1Fan.setTotalEnergyKwh(1.58);

        Appliance r1Tv = new Appliance(room1, "Smart TV", "TV", "tv", 120.0, false);
        r1Tv.setActiveDurationHours(1.8);
        r1Tv.setTotalEnergyKwh(1.08);

        applianceRepository.saveAll(List.of(r1Bulb, r1Fan, r1Tv));

        // Room 2: Bedroom
        Room room2 = new Room("Room 2 - Bedroom", "ROOM_2", "Master bedroom with climate control");
        room2 = roomRepository.save(room2);

        Appliance r2Bulb = new Appliance(room2, "Bedside Warm Bulb", "BULB", "lightbulb", 15.0, false);
        r2Bulb.setActiveDurationHours(1.0);
        r2Bulb.setTotalEnergyKwh(0.12);

        Appliance r2Fan = new Appliance(room2, "Ceiling Fan", "FAN", "fan", 75.0, true);
        r2Fan.setActiveDurationHours(5.5);
        r2Fan.setTotalEnergyKwh(2.15);

        Appliance r2Ac = new Appliance(room2, "Inverter Air Conditioner", "AC", "snowflake", 1500.0, false);
        r2Ac.setActiveDurationHours(3.0);
        r2Ac.setTotalEnergyKwh(6.50);

        applianceRepository.saveAll(List.of(r2Bulb, r2Fan, r2Ac));

        // Room 3: Kitchen & Utility
        Room room3 = new Room("Room 3 - Kitchen & Utility", "ROOM_3", "Cooking, storage and utility appliances");
        room3 = roomRepository.save(room3);

        Appliance r3Light = new Appliance(room3, "Kitchen LED Tube Light", "BULB", "lightbulb", 25.0, true);
        r3Light.setActiveDurationHours(3.0);
        r3Light.setTotalEnergyKwh(0.45);

        Appliance r3Exhaust = new Appliance(room3, "Exhaust Fan", "FAN", "fan", 50.0, false);
        r3Exhaust.setActiveDurationHours(1.2);
        r3Exhaust.setTotalEnergyKwh(0.30);

        Appliance r3Fridge = new Appliance(room3, "Double-Door Refrigerator", "REFRIGERATOR", "kitchen-set", 200.0, true);
        r3Fridge.setActiveDurationHours(12.0);
        r3Fridge.setTotalEnergyKwh(4.80);

        applianceRepository.saveAll(List.of(r3Light, r3Exhaust, r3Fridge));
    }

    private void seedHistoricalEnergyData() {
        List<Appliance> apps = applianceRepository.findAll();
        LocalDate today = LocalDate.now();
        double tariff = 7.50;

        // Generate past 7 days realistic energy logs
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            for (Appliance app : apps) {
                double avgHours = switch (app.getType()) {
                    case "REFRIGERATOR" -> 16.0;
                    case "AC" -> 3.5;
                    case "FAN" -> 6.0;
                    case "TV" -> 2.5;
                    case "BULB" -> 4.0;
                    default -> 2.0;
                };

                // Add slight day-to-day variance
                double dailyHours = avgHours * (0.85 + (Math.random() * 0.3));
                double dailyKwh = (app.getBaseWattage() * dailyHours) / 1000.0;
                double cost = dailyKwh * tariff;

                EnergyUsage usage = new EnergyUsage(
                        app.getRoom(),
                        app,
                        date,
                        Math.round(dailyHours * 10.0) / 10.0,
                        Math.round(dailyKwh * 100.0) / 100.0,
                        Math.round(cost * 10.0) / 10.0
                );
                energyUsageRepository.save(usage);
            }
        }
    }

    private void seedInitialAlertsAndRecommendations() {
        // Sample alert for realistic presentation
        Alert sampleAlert = new Alert(
                "Appliance Running Extended Duration",
                "Ceiling Fan in Room 2 - Bedroom has been operating continuously for over 5 hours.",
                "LONG_RUNNING",
                "WARNING",
                "Room 2 - Bedroom",
                "Ceiling Fan"
        );
        alertRepository.save(sampleAlert);

        // Initial recommendations
        Recommendation rec1 = new Recommendation(
                "Air Conditioner Temperature Optimization",
                "Setting the Inverter AC in Room 2 to 24°C instead of 18°C reduces compressor energy load by approximately 24% without sacrificing comfort.",
                "HIGH_WATTAGE",
                680.0,
                "Set AC thermostat to 24°C"
        );

        Recommendation rec2 = new Recommendation(
                "Bedroom Ventilation Review",
                "The Ceiling Fan in Room 2 has been active continuously for over 5 hours. If the room is empty, turning it off saves ~3.75 kWh weekly.",
                "LONG_RUNNING",
                120.0,
                "Switch off fan in unoccupied rooms"
        );

        Recommendation rec3 = new Recommendation(
                "BLDC Energy Saving Upgrade",
                "Upgrading conventional ceiling fans to 28W 5-star BLDC fans across all 3 rooms can save up to 60% on fan electricity expenses.",
                "SAVING_TIP",
                420.0,
                "Explore 5-Star rated BLDC fans"
        );

        recommendationRepository.saveAll(List.of(rec1, rec2, rec3));
    }
}
