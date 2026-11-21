package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.dto.ApplianceDTO;
import com.smartwatt.dto.ApplianceToggleRequest;
import com.smartwatt.service.ApplianceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appliances")
public class ApplianceController {

    private final ApplianceService applianceService;

    public ApplianceController(ApplianceService applianceService) {
        this.applianceService = applianceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplianceDTO>>> getAllAppliances() {
        List<ApplianceDTO> appliances = applianceService.getAllAppliances();
        return ResponseEntity.ok(ApiResponse.ok("Appliances retrieved successfully", appliances));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<ApplianceDTO>>> getAppliancesByRoom(@PathVariable Long roomId) {
        List<ApplianceDTO> appliances = applianceService.getAppliancesByRoom(roomId);
        return ResponseEntity.ok(ApiResponse.ok("Room appliances retrieved successfully", appliances));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<ApplianceDTO>> toggleAppliance(@PathVariable Long id,
                                                                     @RequestParam(required = false) Boolean state) {
        ApplianceDTO updated = applianceService.toggleAppliance(id, state);
        return ResponseEntity.ok(ApiResponse.ok("Appliance status updated successfully", updated));
    }

    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<ApplianceDTO>> toggleWithBody(@RequestBody ApplianceToggleRequest request) {
        ApplianceDTO updated = applianceService.toggleAppliance(request.getApplianceId(), request.getIsOn());
        return ResponseEntity.ok(ApiResponse.ok("Appliance status updated successfully", updated));
    }
}
