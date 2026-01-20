package com.techanalysis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.dto.ApiResponse;
import com.techanalysis.entity.Paper;
import com.techanalysis.service.PaperService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping
    public ApiResponse<Page<Paper>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(paperService.list(page, size, country, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Paper> getById(@PathVariable Long id) {
        return ApiResponse.success(paperService.getById(id));
    }
}
