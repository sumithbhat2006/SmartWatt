package com.smartwatt.dto;

import com.smartwatt.entity.Alert;
import com.smartwatt.entity.Recommendation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDTO {
    private Double totalPowerWatts = 0.0;
    private Double totalEnergyKwh = 0.0;
    private Double estimatedBillInr = 0.0;
    private int activeAppliancesCount = 0;
    private int totalAppliancesCount = 0;
    private Double tariffRatePerKwh = 7.50;
    private Double powerThresholdWatts = 2500.0;
    private String systemStatus = "NORMAL"; // NORMAL, MODERATE, HIGH_CONSUMPTION, CRITICAL
    private Boolean isSimulationActive = true;
    private Double simulatedGridVoltage = 230.0;
    private Double totalCurrentAmp = 0.0;
    private List<RoomSummaryDTO> rooms = new ArrayList<>();
    private List<Alert> recentAlerts = new ArrayList<>();
    private List<Recommendation> activeRecommendations = new ArrayList<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public DashboardSummaryDTO() {}

    public Double getTotalPowerWatts() {
        return totalPowerWatts;
    }

    public void setTotalPowerWatts(Double totalPowerWatts) {
        this.totalPowerWatts = totalPowerWatts;
    }

    public Double getTotalEnergyKwh() {
        return totalEnergyKwh;
    }

    public void setTotalEnergyKwh(Double totalEnergyKwh) {
        this.totalEnergyKwh = totalEnergyKwh;
    }

    public Double getEstimatedBillInr() {
        return estimatedBillInr;
    }

    public void setEstimatedBillInr(Double estimatedBillInr) {
        this.estimatedBillInr = estimatedBillInr;
    }

    public int getActiveAppliancesCount() {
        return activeAppliancesCount;
    }

    public void setActiveAppliancesCount(int activeAppliancesCount) {
        this.activeAppliancesCount = activeAppliancesCount;
    }

    public int getTotalAppliancesCount() {
        return totalAppliancesCount;
    }

    public void setTotalAppliancesCount(int totalAppliancesCount) {
        this.totalAppliancesCount = totalAppliancesCount;
    }

    public Double getTariffRatePerKwh() {
        return tariffRatePerKwh;
    }

    public void setTariffRatePerKwh(Double tariffRatePerKwh) {
        this.tariffRatePerKwh = tariffRatePerKwh;
    }

    public Double getPowerThresholdWatts() {
        return powerThresholdWatts;
    }

    public void setPowerThresholdWatts(Double powerThresholdWatts) {
        this.powerThresholdWatts = powerThresholdWatts;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public Boolean getIsSimulationActive() {
        return isSimulationActive;
    }

    public void setIsSimulationActive(Boolean simulationActive) {
        isSimulationActive = simulationActive;
    }

    public Double getSimulatedGridVoltage() {
        return simulatedGridVoltage;
    }

    public void setSimulatedGridVoltage(Double simulatedGridVoltage) {
        this.simulatedGridVoltage = simulatedGridVoltage;
    }

    public Double getTotalCurrentAmp() {
        return totalCurrentAmp;
    }

    public void setTotalCurrentAmp(Double totalCurrentAmp) {
        this.totalCurrentAmp = totalCurrentAmp;
    }

    public List<RoomSummaryDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomSummaryDTO> rooms) {
        this.rooms = rooms;
    }

    public List<Alert> getRecentAlerts() {
        return recentAlerts;
    }

    public void setRecentAlerts(List<Alert> recentAlerts) {
        this.recentAlerts = recentAlerts;
    }

    public List<Recommendation> getActiveRecommendations() {
        return activeRecommendations;
    }

    public void setActiveRecommendations(List<Recommendation> activeRecommendations) {
        this.activeRecommendations = activeRecommendations;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
