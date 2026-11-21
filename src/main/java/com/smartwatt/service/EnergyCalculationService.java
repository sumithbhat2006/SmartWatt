package com.smartwatt.service;

import com.smartwatt.dto.EnergyAnalyticsDTO;
import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.EnergyUsage;
import com.smartwatt.entity.Room;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.EnergyUsageRepository;
import com.smartwatt.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EnergyCalculationService {

    private final ApplianceRepository applianceRepository;
    private final RoomRepository roomRepository;
    private final EnergyUsageRepository energyUsageRepository;
    private final SettingsService settingsService;

    public EnergyCalculationService(ApplianceRepository applianceRepository,
                                  RoomRepository roomRepository,
                                  EnergyUsageRepository energyUsageRepository,
                                  SettingsService settingsService) {
        this.applianceRepository = applianceRepository;
        this.roomRepository = roomRepository;
        this.energyUsageRepository = energyUsageRepository;
        this.settingsService = settingsService;
    }

    /**
     * Energy (kWh) = Power (W) * Time (hours) / 1000
     */
    public double calculateKwh(double powerWatts, double timeHours) {
        return (powerWatts * timeHours) / 1000.0;
    }

    public double calculateCost(double kwh, double tariffRate) {
        return Math.round(kwh * tariffRate * 100.0) / 100.0;
    }

    public EnergyAnalyticsDTO getAnalytics() {
        Double tariff = settingsService.getTariffRate();
        List<Room> rooms = roomRepository.findAll();
        List<Appliance> appliances = applianceRepository.findAll();

        EnergyAnalyticsDTO dto = new EnergyAnalyticsDTO();
        dto.setTariffRatePerKwh(tariff);

        double totalCurrentPower = 0.0;
        double totalKwh = 0.0;

        // Populate Appliance consumption
        List<EnergyAnalyticsDTO.ApplianceConsumptionDTO> appDtoList = new ArrayList<>();
        List<EnergyAnalyticsDTO.ApplianceConsumptionDTO> highConsuming = new ArrayList<>();

        for (Appliance app : appliances) {
            double currentWattage = Boolean.TRUE.equals(app.getIsOn()) ?
                    (app.getCurrentWattage() != null ? app.getCurrentWattage() : app.getBaseWattage()) : 0.0;
            totalCurrentPower += currentWattage;

            double appKwh = app.getTotalEnergyKwh() != null ? app.getTotalEnergyKwh() : 0.0;
            totalKwh += appKwh;

            double cost = calculateCost(appKwh, tariff);

            EnergyAnalyticsDTO.ApplianceConsumptionDTO appDto = new EnergyAnalyticsDTO.ApplianceConsumptionDTO();
            appDto.setApplianceId(app.getId());
            appDto.setApplianceName(app.getName());
            appDto.setRoomName(app.getRoom().getName());
            appDto.setType(app.getType());
            appDto.setBaseWattage(app.getBaseWattage());
            appDto.setCurrentWattage(Math.round(currentWattage * 10.0) / 10.0);
            appDto.setTotalKwh(Math.round(appKwh * 1000.0) / 1000.0);
            appDto.setEstimatedCostInr(cost);
            appDto.setIsOn(app.getIsOn());
            appDto.setActiveHours(app.getActiveDurationHours() != null ?
                    Math.round(app.getActiveDurationHours() * 10.0) / 10.0 : 0.0);

            appDtoList.add(appDto);

            // High consuming identification: rated >= 500W or active wattage >= 500W
            if (app.getBaseWattage() >= 500.0 || currentWattage >= 500.0) {
                highConsuming.add(appDto);
            }
        }

        dto.setTotalPowerWatts(Math.round(totalCurrentPower * 10.0) / 10.0);
        dto.setTotalEnergyKwh(Math.round(totalKwh * 1000.0) / 1000.0);
        dto.setTotalCostInr(calculateCost(totalKwh, tariff));
        dto.setApplianceConsumptionList(appDtoList);
        dto.setHighConsumingAppliances(highConsuming);

        // Populate Room consumption
        List<EnergyAnalyticsDTO.RoomConsumptionDTO> roomDtoList = new ArrayList<>();
        for (Room room : rooms) {
            double roomWattage = appliances.stream()
                    .filter(a -> a.getRoom().getId().equals(room.getId()) && Boolean.TRUE.equals(a.getIsOn()))
                    .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : 0.0)
                    .sum();

            double roomKwh = appliances.stream()
                    .filter(a -> a.getRoom().getId().equals(room.getId()))
                    .mapToDouble(a -> a.getTotalEnergyKwh() != null ? a.getTotalEnergyKwh() : 0.0)
                    .sum();

            double pct = totalKwh > 0 ? (roomKwh / totalKwh) * 100.0 : 0.0;

            EnergyAnalyticsDTO.RoomConsumptionDTO rDto = new EnergyAnalyticsDTO.RoomConsumptionDTO(
                    room.getId(),
                    room.getName(),
                    Math.round(roomWattage * 10.0) / 10.0,
                    Math.round(roomKwh * 1000.0) / 1000.0,
                    calculateCost(roomKwh, tariff),
                    Math.round(pct * 10.0) / 10.0
            );
            roomDtoList.add(rDto);
        }
        dto.setRoomConsumptionList(roomDtoList);

        // Daily Trends (last 7 days from energy_usage table)
        LocalDate today = LocalDate.now();
        List<EnergyAnalyticsDTO.DailyTrendDTO> dailyTrends = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, MMM d");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<EnergyUsage> dailyUsages = energyUsageRepository.findByUsageDate(date);
            double dayKwh = dailyUsages.stream().mapToDouble(EnergyUsage::getTotalKwh).sum();
            dailyTrends.add(new EnergyAnalyticsDTO.DailyTrendDTO(
                    date.format(dtf),
                    Math.round(dayKwh * 100.0) / 100.0,
                    calculateCost(dayKwh, tariff)
            ));
        }
        dto.setDailyTrends(dailyTrends);

        // Monthly Breakdown
        Map<String, Double> monthly = new LinkedHashMap<>();
        monthly.put("Current Month Projected (30 Days)", Math.round(totalKwh * 30.0 * 100.0) / 100.0);
        monthly.put("Projected Bill (₹)", Math.round(totalKwh * 30.0 * tariff * 100.0) / 100.0);
        dto.setMonthlyBreakdown(monthly);

        return dto;
    }
}
