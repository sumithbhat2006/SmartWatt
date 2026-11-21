package com.smartwatt.service;

import com.smartwatt.dto.ApplianceDTO;
import com.smartwatt.dto.RoomSummaryDTO;
import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.Room;
import com.smartwatt.repository.ApplianceRepository;
import com.smartwatt.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ApplianceRepository applianceRepository;
    private final SettingsService settingsService;
    private final ApplianceService applianceService;

    public RoomService(RoomRepository roomRepository,
                       ApplianceRepository applianceRepository,
                       SettingsService settingsService,
                       ApplianceService applianceService) {
        this.roomRepository = roomRepository;
        this.applianceRepository = applianceRepository;
        this.settingsService = settingsService;
        this.applianceService = applianceService;
    }

    public List<RoomSummaryDTO> getRoomSummaries() {
        List<Room> rooms = roomRepository.findAll();
        List<Appliance> allAppliances = applianceRepository.findAll();
        Double tariff = settingsService.getTariffRate();

        List<RoomSummaryDTO> summaries = new ArrayList<>();

        for (Room room : rooms) {
            List<Appliance> roomAppliances = allAppliances.stream()
                    .filter(a -> a.getRoom().getId().equals(room.getId()))
                    .collect(Collectors.toList());

            double roomCurrentPower = roomAppliances.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsOn()))
                    .mapToDouble(a -> a.getCurrentWattage() != null ? a.getCurrentWattage() : a.getBaseWattage())
                    .sum();

            double roomTotalKwh = roomAppliances.stream()
                    .mapToDouble(a -> a.getTotalEnergyKwh() != null ? a.getTotalEnergyKwh() : 0.0)
                    .sum();

            int activeCount = (int) roomAppliances.stream().filter(a -> Boolean.TRUE.equals(a.getIsOn())).count();

            List<ApplianceDTO> appDtos = roomAppliances.stream()
                    .map(applianceService::mapToDTO)
                    .collect(Collectors.toList());

            RoomSummaryDTO summary = new RoomSummaryDTO();
            summary.setId(room.getId());
            summary.setName(room.getName());
            summary.setSectionCode(room.getSectionCode());
            summary.setDescription(room.getDescription());
            summary.setCurrentPowerWatts(Math.round(roomCurrentPower * 10.0) / 10.0);
            summary.setTotalEnergyKwh(Math.round(roomTotalKwh * 1000.0) / 1000.0);
            summary.setEstimatedCostInr(Math.round(roomTotalKwh * tariff * 100.0) / 100.0);
            summary.setActiveAppliancesCount(activeCount);
            summary.setTotalAppliancesCount(roomAppliances.size());
            summary.setAppliances(appDtos);

            summaries.add(summary);
        }

        return summaries;
    }
}
