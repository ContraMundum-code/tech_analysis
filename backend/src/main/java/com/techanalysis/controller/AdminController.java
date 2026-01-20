package com.techanalysis.controller;

import com.techanalysis.dto.ApiResponse;
import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import com.techanalysis.service.ExcelImportService;
import com.techanalysis.service.StreamingExcelImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ExcelImportService excelImportService;
    private final StreamingExcelImportService streamingExcelImportService;

    public AdminController(ExcelImportService excelImportService, StreamingExcelImportService streamingExcelImportService) {
        this.excelImportService = excelImportService;
        this.streamingExcelImportService = streamingExcelImportService;
    }

    // 流式导入（大文件推荐，内存占用低）
    @PostMapping("/import/patent")
    public ApiResponse<Map<String, Object>> importPatent(@RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "streaming", defaultValue = "true") boolean streaming) {
        try {
            log.info("开始导入专利数据，文件大小: {} MB, 流式模式: {}", file.getSize() / 1024 / 1024, streaming);
            long startTime = System.currentTimeMillis();
            
            int imported;
            if (streaming) {
                imported = streamingExcelImportService.importPatentStreaming(file);
            } else {
                List<Patent> patents = excelImportService.parsePatentExcel(file);
                imported = excelImportService.importPatents(patents);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("专利导入完成，共 {} 条，耗时 {} ms", imported, duration);
            
            Map<String, Object> result = new HashMap<>();
            result.put("imported", imported);
            result.put("duration", duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("专利导入失败", e);
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/import/project")
    public ApiResponse<Map<String, Object>> importProject(@RequestParam("file") MultipartFile file,
                                                           @RequestParam(value = "streaming", defaultValue = "true") boolean streaming) {
        try {
            log.info("开始导入项目数据，文件大小: {} MB, 流式模式: {}", file.getSize() / 1024 / 1024, streaming);
            long startTime = System.currentTimeMillis();
            
            int imported;
            if (streaming) {
                imported = streamingExcelImportService.importProjectStreaming(file);
            } else {
                List<Project> projects = excelImportService.parseProjectExcel(file);
                imported = excelImportService.importProjects(projects);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("项目导入完成，共 {} 条，耗时 {} ms", imported, duration);
            
            Map<String, Object> result = new HashMap<>();
            result.put("imported", imported);
            result.put("duration", duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("项目导入失败", e);
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/import/paper")
    public ApiResponse<Map<String, Object>> importPaper(@RequestParam("file") MultipartFile file,
                                                         @RequestParam(value = "streaming", defaultValue = "true") boolean streaming) {
        try {
            log.info("开始导入论文数据，文件大小: {} MB, 流式模式: {}", file.getSize() / 1024 / 1024, streaming);
            long startTime = System.currentTimeMillis();
            
            int imported;
            if (streaming) {
                imported = streamingExcelImportService.importPaperStreaming(file);
            } else {
                List<Paper> papers = excelImportService.parsePaperExcel(file);
                imported = excelImportService.importPapers(papers);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("论文导入完成，共 {} 条，耗时 {} ms", imported, duration);
            
            Map<String, Object> result = new HashMap<>();
            result.put("imported", imported);
            result.put("duration", duration);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("论文导入失败", e);
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    // 保留旧接口兼容
    @PostMapping("/import/patent/preview")
    public ApiResponse<List<Patent>> previewPatent(@RequestParam("file") MultipartFile file) throws Exception {
        List<Patent> patents = excelImportService.parsePatentExcel(file);
        // 只返回前100条预览
        return ApiResponse.success(patents.size() > 100 ? patents.subList(0, 100) : patents);
    }

    @PostMapping("/import/patent/confirm")
    public ApiResponse<Map<String, Integer>> confirmPatent(@RequestBody List<Patent> patents) {
        excelImportService.importPatents(patents);
        return ApiResponse.success(Map.of("imported", patents.size()));
    }

    @PostMapping("/import/project/preview")
    public ApiResponse<List<Project>> previewProject(@RequestParam("file") MultipartFile file) throws Exception {
        List<Project> projects = excelImportService.parseProjectExcel(file);
        return ApiResponse.success(projects.size() > 100 ? projects.subList(0, 100) : projects);
    }

    @PostMapping("/import/project/confirm")
    public ApiResponse<Map<String, Integer>> confirmProject(@RequestBody List<Project> projects) {
        excelImportService.importProjects(projects);
        return ApiResponse.success(Map.of("imported", projects.size()));
    }

    @PostMapping("/import/paper/preview")
    public ApiResponse<List<Paper>> previewPaper(@RequestParam("file") MultipartFile file) throws Exception {
        List<Paper> papers = excelImportService.parsePaperExcel(file);
        return ApiResponse.success(papers.size() > 100 ? papers.subList(0, 100) : papers);
    }

    @PostMapping("/import/paper/confirm")
    public ApiResponse<Map<String, Integer>> confirmPaper(@RequestBody List<Paper> papers) {
        excelImportService.importPapers(papers);
        return ApiResponse.success(Map.of("imported", papers.size()));
    }
}
