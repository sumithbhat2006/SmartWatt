package com.smartwatt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores sensor readings (simulated in Stage 2, ready for ESP32/IoT sensors in future stages).
 */
@Entity
@Table(name = "readings")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appliance_id", nullable = false)
    private Appliance appliance;

    @Column(nullable = false)
    private Double voltage; // In Volts (e.g. 230.1 V)

    @Column(name = "current_amp", nullable = false)
    private Double currentAmp; // In Amperes (e.g. 0.33 A)

    @Column(name = "power_watt", nullable = false)
    private Double powerWatt; // In Watts (e.g. 75.9 W)

    @Column(name = "appliance_status", nullable = false)
    private Boolean applianceStatus; // true = ON, false = OFF

    @Column(name = "is_simulated", nullable = false)
    private Boolean isSimulated = true;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    public Reading() {}

    public Reading(Appliance appliance, Double voltage, Double currentAmp, Double powerWatt, Boolean applianceStatus, Boolean isSimulated) {
        this.appliance = appliance;
        this.voltage = voltage;
        this.currentAmp = currentAmp;
        this.powerWatt = powerWatt;
        this.applianceStatus = applianceStatus;
        this.isSimulated = isSimulated;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public Double getCurrentAmp() {
        return currentAmp;
    }

    public void setCurrentAmp(Double currentAmp) {
        this.currentAmp = currentAmp;
    }

    public Double getPowerWatt() {
        return powerWatt;
    }

    public void setPowerWatt(Double powerWatt) {
        this.powerWatt = powerWatt;
    }

    public Boolean getApplianceStatus() {
        return applianceStatus;
    }

    public void setApplianceStatus(Boolean applianceStatus) {
        this.applianceStatus = applianceStatus;
    }

    public Boolean getIsSimulated() {
        return isSimulated;
    }

    public void setIsSimulated(Boolean simulated) {
        isSimulated = simulated;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
