package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.entity.Alert;
import com.smartwatt.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Alert>>> getActiveAlerts() {
        List<Alert> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(ApiResponse.ok("Active alerts retrieved successfully", alerts));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Alert>>> getRecentAlerts(@RequestParam(defaultValue = "10") int limit) {
        List<Alert> alerts = alertService.getRecentAlerts(limit);
        return ResponseEntity.ok(ApiResponse.ok("Recent alerts retrieved successfully", alerts));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveAlert(@PathVariable Long id) {
        alertService.resolveAlert(id);
        return ResponseEntity.ok(ApiResponse.ok("Alert resolved successfully", "RESOLVED"));
    }

    @PostMapping("/resolve-all")
    public ResponseEntity<ApiResponse<String>> resolveAllAlerts() {
        alertService.resolveAllAlerts();
        return ResponseEntity.ok(ApiResponse.ok("All alerts resolved successfully", "ALL_RESOLVED"));
    }
}
