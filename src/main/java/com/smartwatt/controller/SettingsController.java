package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.dto.TariffSettingsDTO;
import com.smartwatt.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TariffSettingsDTO>> getSettings() {
        TariffSettingsDTO settings = settingsService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.ok("Settings retrieved successfully", settings));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TariffSettingsDTO>> updateSettings(@RequestBody TariffSettingsDTO dto) {
        TariffSettingsDTO updated = settingsService.updateSettings(dto);
        return ResponseEntity.ok(ApiResponse.ok("Settings updated successfully", updated));
    }
}
