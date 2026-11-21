package com.smartwatt.dto;

public class TariffSettingsDTO {
    private Double tariffRatePerKwh;
    private Double maxPowerThresholdWatts;
    private Double roomPowerThresholdWatts;
    private Double longRunningHoursThreshold;

    public TariffSettingsDTO() {}

    public TariffSettingsDTO(Double tariffRatePerKwh, Double maxPowerThresholdWatts, Double roomPowerThresholdWatts, Double longRunningHoursThreshold) {
        this.tariffRatePerKwh = tariffRatePerKwh;
        this.maxPowerThresholdWatts = maxPowerThresholdWatts;
        this.roomPowerThresholdWatts = roomPowerThresholdWatts;
        this.longRunningHoursThreshold = longRunningHoursThreshold;
    }

    public Double getTariffRatePerKwh() {
        return tariffRatePerKwh;
    }

    public void setTariffRatePerKwh(Double tariffRatePerKwh) {
        this.tariffRatePerKwh = tariffRatePerKwh;
    }

    public Double getMaxPowerThresholdWatts() {
        return maxPowerThresholdWatts;
    }

    public void setMaxPowerThresholdWatts(Double maxPowerThresholdWatts) {
        this.maxPowerThresholdWatts = maxPowerThresholdWatts;
    }

    public Double getRoomPowerThresholdWatts() {
        return roomPowerThresholdWatts;
    }

    public void setRoomPowerThresholdWatts(Double roomPowerThresholdWatts) {
        this.roomPowerThresholdWatts = roomPowerThresholdWatts;
    }

    public Double getLongRunningHoursThreshold() {
        return longRunningHoursThreshold;
    }

    public void setLongRunningHoursThreshold(Double longRunningHoursThreshold) {
        this.longRunningHoursThreshold = longRunningHoursThreshold;
    }
}
