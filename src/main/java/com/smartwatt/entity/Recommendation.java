package com.smartwatt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores intelligent, rule-based recommendations to optimize energy usage and reduce electricity bills.
 */
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 600)
    private String description;

    @Column(nullable = false, length = 50)
    private String category; // LONG_RUNNING, HIGH_WATTAGE, ROOM_BALANCE, SAVING_TIP

    @Column(name = "potential_monthly_savings_inr")
    private Double potentialMonthlySavingsInr = 0.0;

    @Column(name = "action_suggested", length = 255)
    private String actionSuggested;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Recommendation() {}

    public Recommendation(String title, String description, String category, Double potentialMonthlySavingsInr, String actionSuggested) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.potentialMonthlySavingsInr = potentialMonthlySavingsInr;
        this.actionSuggested = actionSuggested;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPotentialMonthlySavingsInr() {
        return potentialMonthlySavingsInr;
    }

    public void setPotentialMonthlySavingsInr(Double potentialMonthlySavingsInr) {
        this.potentialMonthlySavingsInr = potentialMonthlySavingsInr;
    }

    public String getActionSuggested() {
        return actionSuggested;
    }

    public void setActionSuggested(String actionSuggested) {
        this.actionSuggested = actionSuggested;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
