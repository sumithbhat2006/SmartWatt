package com.smartwatt.dto;

import java.util.ArrayList;
import java.util.List;

public class RoomSummaryDTO {
    private Long id;
    private String name;
    private String sectionCode;
    private String description;
    private Double currentPowerWatts = 0.0;
    private Double totalEnergyKwh = 0.0;
    private Double estimatedCostInr = 0.0;
    private int activeAppliancesCount = 0;
    private int totalAppliancesCount = 0;
    private List<ApplianceDTO> appliances = new ArrayList<>();

    public RoomSummaryDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentPowerWatts() {
        return currentPowerWatts;
    }

    public void setCurrentPowerWatts(Double currentPowerWatts) {
        this.currentPowerWatts = currentPowerWatts;
    }

    public Double getTotalEnergyKwh() {
        return totalEnergyKwh;
    }

    public void setTotalEnergyKwh(Double totalEnergyKwh) {
        this.totalEnergyKwh = totalEnergyKwh;
    }

    public Double getEstimatedCostInr() {
        return estimatedCostInr;
    }

    public void setEstimatedCostInr(Double estimatedCostInr) {
        this.estimatedCostInr = estimatedCostInr;
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

    public List<ApplianceDTO> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<ApplianceDTO> appliances) {
        this.appliances = appliances;
    }
}
