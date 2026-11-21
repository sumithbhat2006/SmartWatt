package com.smartwatt.dto;

import java.time.LocalDateTime;

public class ApplianceDTO {
    private Long id;
    private Long roomId;
    private String roomName;
    private String name;
    private String type;
    private String icon;
    private Double baseWattage;
    private Boolean isOn;
    private Double currentWattage;
    private Double currentVoltage;
    private Double currentAmp;
    private Double activeDurationHours;
    private Double totalEnergyKwh;
    private LocalDateTime lastStatusChange;

    public ApplianceDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getBaseWattage() {
        return baseWattage;
    }

    public void setBaseWattage(Double baseWattage) {
        this.baseWattage = baseWattage;
    }

    public Boolean getIsOn() {
        return isOn;
    }

    public void setIsOn(Boolean on) {
        isOn = on;
    }

    public Double getCurrentWattage() {
        return currentWattage;
    }

    public void setCurrentWattage(Double currentWattage) {
        this.currentWattage = currentWattage;
    }

    public Double getCurrentVoltage() {
        return currentVoltage;
    }

    public void setCurrentVoltage(Double currentVoltage) {
        this.currentVoltage = currentVoltage;
    }

    public Double getCurrentAmp() {
        return currentAmp;
    }

    public void setCurrentAmp(Double currentAmp) {
        this.currentAmp = currentAmp;
    }

    public Double getActiveDurationHours() {
        return activeDurationHours;
    }

    public void setActiveDurationHours(Double activeDurationHours) {
        this.activeDurationHours = activeDurationHours;
    }

    public Double getTotalEnergyKwh() {
        return totalEnergyKwh;
    }

    public void setTotalEnergyKwh(Double totalEnergyKwh) {
        this.totalEnergyKwh = totalEnergyKwh;
    }

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(LocalDateTime lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }
}
