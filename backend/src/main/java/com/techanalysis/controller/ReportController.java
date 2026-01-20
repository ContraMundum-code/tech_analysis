package com.techanalysis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.dto.ApiResponse;
import com.techanalysis.dto.ReportRequest;
import com.techanalysis.entity.Report;
import com.techanalysis.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/list")
    public ApiResponse<Page<Report>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(reportService.list(userId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Report> getById(@PathVariable Long id) {
        return ApiResponse.success(reportService.getById(id));
    }

    @PostMapping("/generate")
    public ApiResponse<Map<String, String>> generate(@RequestBody ReportRequest request) {
        String content = reportService.generateReport(request.getTopic(), request.getRequirements());
        return ApiResponse.success(Map.of("content", content));
    }

    @PostMapping("/save")
    public ApiResponse<Report> save(@RequestBody Report report) {
        return ApiResponse.success(reportService.create(report));
    }
}
