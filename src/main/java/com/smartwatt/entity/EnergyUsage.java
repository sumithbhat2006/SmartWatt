package com.smartwatt.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Stores aggregated energy consumption records (daily / monthly tracking).
 * Energy (kWh) = Power (W) * Time (hours) / 1000
 */
@Entity
@Table(name = "energy_usage")
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appliance_id")
    private Appliance appliance;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate = LocalDate.now();

    @Column(name = "active_hours", nullable = false)
    private Double activeHours = 0.0;

    @Column(name = "total_kwh", nullable = false)
    private Double totalKwh = 0.0;

    @Column(name = "calculated_cost", nullable = false)
    private Double calculatedCost = 0.0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public EnergyUsage() {}

    public EnergyUsage(Room room, Appliance appliance, LocalDate usageDate, Double activeHours, Double totalKwh, Double calculatedCost) {
        this.room = room;
        this.appliance = appliance;
        this.usageDate = usageDate;
        this.activeHours = activeHours;
        this.totalKwh = totalKwh;
        this.calculatedCost = calculatedCost;
        this.updatedAt = LocalDateTime.now();
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

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public Double getActiveHours() {
        return activeHours;
    }

    public void setActiveHours(Double activeHours) {
        this.activeHours = activeHours;
    }

    public Double getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(Double totalKwh) {
        this.totalKwh = totalKwh;
    }

    public Double getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(Double calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
