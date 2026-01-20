package com.techanalysis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.dto.ApiResponse;
import com.techanalysis.entity.Patent;
import com.techanalysis.service.PatentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patents")
public class PatentController {

    private final PatentService patentService;

    public PatentController(PatentService patentService) {
        this.patentService = patentService;
    }

    @GetMapping
    public ApiResponse<Page<Patent>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(patentService.list(page, size, country, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Patent> getById(@PathVariable Long id) {
        return ApiResponse.success(patentService.getById(id));
    }
}
