package com.smartwatt.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnergyAnalyticsDTO {
    private Double totalPowerWatts = 0.0;
    private Double totalEnergyKwh = 0.0;
    private Double totalCostInr = 0.0;
    private Double tariffRatePerKwh = 7.50;

    // Room-wise data for charts and tables
    private List<RoomConsumptionDTO> roomConsumptionList = new ArrayList<>();

    // Appliance-wise data
    private List<ApplianceConsumptionDTO> applianceConsumptionList = new ArrayList<>();

    // High consuming appliances (> 500W base or top usage)
    private List<ApplianceConsumptionDTO> highConsumingAppliances = new ArrayList<>();

    // Historical trends
    private List<DailyTrendDTO> dailyTrends = new ArrayList<>();
    private Map<String, Double> monthlyBreakdown;

    public EnergyAnalyticsDTO() {}

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

    public Double getTotalCostInr() {
        return totalCostInr;
    }

    public void setTotalCostInr(Double totalCostInr) {
        this.totalCostInr = totalCostInr;
    }

    public Double getTariffRatePerKwh() {
        return tariffRatePerKwh;
    }

    public void setTariffRatePerKwh(Double tariffRatePerKwh) {
        this.tariffRatePerKwh = tariffRatePerKwh;
    }

    public List<RoomConsumptionDTO> getRoomConsumptionList() {
        return roomConsumptionList;
    }

    public void setRoomConsumptionList(List<RoomConsumptionDTO> roomConsumptionList) {
        this.roomConsumptionList = roomConsumptionList;
    }

    public List<ApplianceConsumptionDTO> getApplianceConsumptionList() {
        return applianceConsumptionList;
    }

    public void setApplianceConsumptionList(List<ApplianceConsumptionDTO> applianceConsumptionList) {
        this.applianceConsumptionList = applianceConsumptionList;
    }

    public List<ApplianceConsumptionDTO> getHighConsumingAppliances() {
        return highConsumingAppliances;
    }

    public void setHighConsumingAppliances(List<ApplianceConsumptionDTO> highConsumingAppliances) {
        this.highConsumingAppliances = highConsumingAppliances;
    }

    public List<DailyTrendDTO> getDailyTrends() {
        return dailyTrends;
    }

    public void setDailyTrends(List<DailyTrendDTO> dailyTrends) {
        this.dailyTrends = dailyTrends;
    }

    public Map<String, Double> getMonthlyBreakdown() {
        return monthlyBreakdown;
    }

    public void setMonthlyBreakdown(Map<String, Double> monthlyBreakdown) {
        this.monthlyBreakdown = monthlyBreakdown;
    }

    // Inner DTO for Room consumption
    public static class RoomConsumptionDTO {
        private Long roomId;
        private String roomName;
        private Double currentPowerWatts;
        private Double totalKwh;
        private Double estimatedCostInr;
        private Double percentageOfTotal;

        public RoomConsumptionDTO() {}

        public RoomConsumptionDTO(Long roomId, String roomName, Double currentPowerWatts, Double totalKwh, Double estimatedCostInr, Double percentageOfTotal) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.currentPowerWatts = currentPowerWatts;
            this.totalKwh = totalKwh;
            this.estimatedCostInr = estimatedCostInr;
            this.percentageOfTotal = percentageOfTotal;
        }

        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public Double getCurrentPowerWatts() {
            return currentPowerWatts;
        }

        public void setCurrentPowerWatts(Double currentPowerWatts) {
            this.currentPowerWatts = currentPowerWatts;
        }

        public Double getTotalKwh() {
            return totalKwh;
        }

        public void setTotalKwh(Double totalKwh) {
            this.totalKwh = totalKwh;
        }

        public Double getEstimatedCostInr() {
            return estimatedCostInr;
        }

        public void setEstimatedCostInr(Double estimatedCostInr) {
            this.estimatedCostInr = estimatedCostInr;
        }

        public Double getPercentageOfTotal() {
            return percentageOfTotal;
        }

        public void setPercentageOfTotal(Double percentageOfTotal) {
            this.percentageOfTotal = percentageOfTotal;
        }
    }

    // Inner DTO for Appliance consumption
    public static class ApplianceConsumptionDTO {
        private Long applianceId;
        private String applianceName;
        private String roomName;
        private String type;
        private Double baseWattage;
        private Double currentWattage;
        private Double totalKwh;
        private Double estimatedCostInr;
        private Boolean isOn;
        private Double activeHours;

        public ApplianceConsumptionDTO() {}

        public Long getApplianceId() {
            return applianceId;
        }

        public void setApplianceId(Long applianceId) {
            this.applianceId = applianceId;
        }

        public String getApplianceName() {
            return applianceName;
        }

        public void setApplianceName(String applianceName) {
            this.applianceName = applianceName;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getBaseWattage() {
            return baseWattage;
        }

        public void setBaseWattage(Double baseWattage) {
            this.baseWattage = baseWattage;
        }

        public Double getCurrentWattage() {
            return currentWattage;
        }

        public void setCurrentWattage(Double currentWattage) {
            this.currentWattage = currentWattage;
        }

        public Double getTotalKwh() {
            return totalKwh;
        }

        public void setTotalKwh(Double totalKwh) {
            this.totalKwh = totalKwh;
        }

        public Double getEstimatedCostInr() {
            return estimatedCostInr;
        }

        public void setEstimatedCostInr(Double estimatedCostInr) {
            this.estimatedCostInr = estimatedCostInr;
        }

        public Boolean getIsOn() {
            return isOn;
        }

        public void setIsOn(Boolean on) {
            isOn = on;
        }

        public Double getActiveHours() {
            return activeHours;
        }

        public void setActiveHours(Double activeHours) {
            this.activeHours = activeHours;
        }
    }

    // Inner DTO for Daily Trends
    public static class DailyTrendDTO {
        private String dateLabel;
        private Double kwh;
        private Double costInr;

        public DailyTrendDTO() {}

        public DailyTrendDTO(String dateLabel, Double kwh, Double costInr) {
            this.dateLabel = dateLabel;
            this.kwh = kwh;
            this.costInr = costInr;
        }

        public String getDateLabel() {
            return dateLabel;
        }

        public void setDateLabel(String dateLabel) {
            this.dateLabel = dateLabel;
        }

        public Double getKwh() {
            return kwh;
        }

        public void setKwh(Double kwh) {
            this.kwh = kwh;
        }

        public Double getCostInr() {
            return costInr;
        }

        public void setCostInr(Double costInr) {
            this.costInr = costInr;
        }
    }
}
