package com.techanalysis.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.dto.ApiResponse;
import com.techanalysis.entity.Project;
import com.techanalysis.service.ProjectService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<Page<Project>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.success(projectService.list(page, size, country, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Project> getById(@PathVariable Long id) {
        return ApiResponse.success(projectService.getById(id));
    }
}
