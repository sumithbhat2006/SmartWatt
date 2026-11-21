package com.smartwatt.service;

import com.smartwatt.entity.Alert;
import com.smartwatt.entity.Appliance;
import com.smartwatt.entity.Room;
import com.smartwatt.repository.AlertRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final SettingsService settingsService;

    public AlertService(AlertRepository alertRepository, SettingsService settingsService) {
        this.alertRepository = alertRepository;
        this.settingsService = settingsService;
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByTimestampDesc();
    }

    public List<Alert> getRecentAlerts(int limit) {
        return alertRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit));
    }

    @Transactional
    public void resolveAlert(Long alertId) {
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.setIsResolved(true);
            alertRepository.save(alert);
        });
    }

    @Transactional
    public void resolveAllAlerts() {
        List<Alert> unresolved = alertRepository.findByIsResolvedFalseOrderByTimestampDesc();
        for (Alert alert : unresolved) {
            alert.setIsResolved(true);
        }
        alertRepository.saveAll(unresolved);
    }

    @Transactional
    public void evaluateTotalPower(Double totalWatts) {
        Double maxThreshold = settingsService.getMaxPowerThreshold();
        if (totalWatts > maxThreshold) {
            String title = "High Total Power Load";
            String message = String.format("Total power consumption of %.1f W exceeded the safety threshold of %.1f W.",
                    totalWatts, maxThreshold);
            createAlertIfNotRecent(title, message, "EXCESSIVE_POWER", "CRITICAL", "All Rooms", "System-Wide");
        }
    }

    @Transactional
    public void evaluateRoomPower(Room room, Double roomWatts) {
        Double roomThreshold = settingsService.getRoomPowerThreshold();
        if (roomWatts > roomThreshold) {
            String title = "High Room Consumption";
            String message = String.format("%s is consuming %.1f W, exceeding room limit of %.1f W.",
                    room.getName(), roomWatts, roomThreshold);
            createAlertIfNotRecent(title, message, "HIGH_ROOM_CONSUMPTION", "WARNING", room.getName(), null);
        }
    }

    @Transactional
    public void evaluateApplianceDuration(Appliance appliance) {
        if (Boolean.TRUE.equals(appliance.getIsOn())) {
            Double thresholdHours = settingsService.getLongRunningHoursThreshold();
            if (appliance.getActiveDurationHours() != null && appliance.getActiveDurationHours() >= thresholdHours) {
                String title = "Appliance Running Too Long";
                String message = String.format("%s in %s has been active for %.1f continuous hours.",
                        appliance.getName(), appliance.getRoom().getName(), appliance.getActiveDurationHours());
                createAlertIfNotRecent(title, message, "LONG_RUNNING", "WARNING", appliance.getRoom().getName(), appliance.getName());
            }
        }
    }

    private void createAlertIfNotRecent(String title, String message, String type, String severity, String roomName, String applianceName) {
        List<Alert> unresolved = alertRepository.findByIsResolvedFalseOrderByTimestampDesc();
        // Avoid duplicate spam alerts within the last 5 minutes with the same title
        boolean exists = unresolved.stream().anyMatch(a ->
                a.getTitle().equalsIgnoreCase(title) &&
                a.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(5))
        );

        if (!exists) {
            Alert alert = new Alert(title, message, type, severity, roomName, applianceName);
            alertRepository.save(alert);
        }
    }
}
