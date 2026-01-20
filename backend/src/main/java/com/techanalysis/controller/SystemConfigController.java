package com.techanalysis.controller;

import com.techanalysis.dto.ApiResponse;
import com.techanalysis.dto.SystemConfigDTO;
import com.techanalysis.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config")
public class SystemConfigController {

    private final SystemConfigService configService;

    public SystemConfigController(SystemConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ApiResponse<SystemConfigDTO> getConfig() {
        return ApiResponse.success(configService.getConfig());
    }

    @PostMapping
    public ApiResponse<Void> saveConfig(@RequestBody SystemConfigDTO dto) {
        configService.saveConfig(dto);
        return ApiResponse.success(null);
    }
}
