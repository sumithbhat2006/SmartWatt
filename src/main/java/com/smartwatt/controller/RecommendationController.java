package com.smartwatt.controller;

import com.smartwatt.dto.ApiResponse;
import com.smartwatt.entity.Recommendation;
import com.smartwatt.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Recommendation>>> getActiveRecommendations() {
        List<Recommendation> recommendations = recommendationService.getActiveRecommendations();
        return ResponseEntity.ok(ApiResponse.ok("Active recommendations retrieved successfully", recommendations));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<ApiResponse<String>> dismissRecommendation(@PathVariable Long id) {
        recommendationService.dismissRecommendation(id);
        return ResponseEntity.ok(ApiResponse.ok("Recommendation dismissed", "DISMISSED"));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<Recommendation>>> generateRecommendations() {
        recommendationService.generateIntelligentRecommendations();
        List<Recommendation> recommendations = recommendationService.getActiveRecommendations();
        return ResponseEntity.ok(ApiResponse.ok("Recommendations evaluated successfully", recommendations));
    }
}
