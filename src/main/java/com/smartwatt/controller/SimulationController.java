package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.service.SettingsService;
import com.smartwatt.simulation.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;
    private final SettingsService settingsService;

    public SimulationController(SimulationService simulationService, SettingsService settingsService) {
        this.simulationService = simulationService;
        this.settingsService = settingsService;
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSimulationStatus() {
        boolean enabled = settingsService.isSimulationEnabled();
        double voltage = simulationService.getCurrentGridVoltage();
        Map<String, Object> status = Map.of(
                "enabled", enabled,
                "currentVoltage", voltage,
                "statusLabel", enabled ? "SIMULATION ACTIVE" : "SIMULATION PAUSED"
        );
        return ResponseEntity.ok(ApiResponse.ok("Simulation status retrieved", status));
    }

    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleSimulation(@RequestParam(required = false) Boolean enabled) {
        boolean newState = enabled != null ? enabled : !settingsService.isSimulationEnabled();
        settingsService.setSimulationEnabled(newState);
        Map<String, Object> status = Map.of(
                "enabled", newState,
                "statusLabel", newState ? "SIMULATION ACTIVE" : "SIMULATION PAUSED"
        );
        return ResponseEntity.ok(ApiResponse.ok("Simulation state toggled", status));
    }

    @PostMapping("/tick")
    public ResponseEntity<ApiResponse<String>> triggerTick() {
        simulationService.runSimulationTick();
        return ResponseEntity.ok(ApiResponse.ok("Simulation tick executed successfully", "OK"));
    }
}
