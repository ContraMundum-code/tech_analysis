package com.techanalysis.controller;

import com.techanalysis.dto.*;
import com.techanalysis.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/techmap")
    public ApiResponse<TechMapData> getTechMap(
            @RequestParam(defaultValue = "patent") String dataType,
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getTechMapData(dataType, categoryCode));
    }

    @GetMapping("/trend")
    public ApiResponse<TrendData> getTrend(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getTrendData(country, startYear, endYear, categoryCode));
    }

    @GetMapping("/competition")
    public ApiResponse<CompetitionData> getCompetition(
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getCompetitionData(categoryCode));
    }

    @GetMapping("/comparison")
    public ApiResponse<ComparisonData> getComparison(
            @RequestParam(defaultValue = "中国") String countryA,
            @RequestParam(defaultValue = "美国") String countryB) {
        return ApiResponse.success(analysisService.getComparisonData(countryA, countryB));
    }

    @GetMapping("/network")
    public ApiResponse<NetworkData> getNetwork(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getNetworkData(country, categoryCode));
    }
    
    @GetMapping("/hottech")
    public ApiResponse<HotTechData> getHotTech(
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getHotTechData(categoryCode));
    }
    
    @GetMapping("/maturity")
    public ApiResponse<TechMaturityData> getMaturity(
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getTechMaturityData(categoryCode));
    }
    
    @GetMapping("/region")
    public ApiResponse<RegionDistributionData> getRegion(
            @RequestParam(required = false) String categoryCode) {
        return ApiResponse.success(analysisService.getRegionDistributionData(categoryCode));
    }
}
