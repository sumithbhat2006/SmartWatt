package com.smartwatt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an electrical appliance located in a specific room.
 */
@Entity
@Table(name = "appliances")
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type; // BULB, FAN, AC, TV, REFRIGERATOR, etc.

    @Column(length = 50)
    private String icon;

    @Column(name = "base_wattage", nullable = false)
    private Double baseWattage; // e.g., 15W, 75W, 1500W

    @Column(name = "is_on", nullable = false)
    private Boolean isOn = false;

    @Column(name = "current_wattage")
    private Double currentWattage = 0.0;

    @Column(name = "current_voltage")
    private Double currentVoltage = 0.0;

    @Column(name = "current_amp")
    private Double currentAmp = 0.0;

    @Column(name = "last_status_change")
    private LocalDateTime lastStatusChange = LocalDateTime.now();

    @Column(name = "active_duration_hours")
    private Double activeDurationHours = 0.0;

    @Column(name = "total_energy_kwh")
    private Double totalEnergyKwh = 0.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Appliance() {}

    public Appliance(Room room, String name, String type, String icon, Double baseWattage, Boolean isOn) {
        this.room = room;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.baseWattage = baseWattage;
        this.isOn = isOn;
        this.currentWattage = isOn ? baseWattage : 0.0;
        this.lastStatusChange = LocalDateTime.now();
        this.activeDurationHours = 0.0;
        this.totalEnergyKwh = 0.0;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(LocalDateTime lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
