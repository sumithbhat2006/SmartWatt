package com.smartwatt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores system and energy alerts for high consumption, threshold breaches, and long-running appliances.
 */
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType; // EXCESSIVE_POWER, HIGH_ROOM_CONSUMPTION, LONG_RUNNING

    @Column(nullable = false, length = 20)
    private String severity; // INFO, WARNING, CRITICAL

    @Column(name = "room_name", length = 100)
    private String roomName;

    @Column(name = "appliance_name", length = 100)
    private String applianceName;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;

    public Alert() {}

    public Alert(String title, String message, String alertType, String severity, String roomName, String applianceName) {
        this.title = title;
        this.message = message;
        this.alertType = alertType;
        this.severity = severity;
        this.roomName = roomName;
        this.applianceName = applianceName;
        this.timestamp = LocalDateTime.now();
        this.isResolved = false;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean resolved) {
        isResolved = resolved;
    }
}
