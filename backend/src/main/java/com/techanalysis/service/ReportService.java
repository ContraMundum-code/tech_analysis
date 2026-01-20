package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techanalysis.entity.Report;
import com.techanalysis.repository.ReportMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    
    private static final String PROMPT_TEMPLATE_PATH = "templates/report-prompt.template";
    private static final String FALLBACK_TEMPLATE_PATH = "templates/report-fallback.template";

    private final ReportMapper reportMapper;
    private final SystemConfigService configService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private String promptTemplate;
    private String fallbackTemplate;

    public ReportService(ReportMapper reportMapper, SystemConfigService configService) {
        this.reportMapper = reportMapper;
        this.configService = configService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // 加载模板
        this.promptTemplate = loadTemplate(PROMPT_TEMPLATE_PATH);
        this.fallbackTemplate = loadTemplate(FALLBACK_TEMPLATE_PATH);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                log.error("模板文件不存在: {}", path);
                return null;
            }
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            log.info("模板文件加载成功: {}", path);
            return content;
        } catch (IOException e) {
            log.error("加载模板文件失败: {}", path, e);
            return null;
        }
    }

    public void reloadTemplates() {
        this.promptTemplate = loadTemplate(PROMPT_TEMPLATE_PATH);
        this.fallbackTemplate = loadTemplate(FALLBACK_TEMPLATE_PATH);
    }

    public Page<Report> list(Long userId, int page, int size) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Report::getUserId, userId);
        }
        wrapper.orderByDesc(Report::getCreatedAt);
        return reportMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Report getById(Long id) {
        return reportMapper.selectById(id);
    }

    public Report create(Report report) {
        report.setStatus("DRAFT");
        reportMapper.insert(report);
        return report;
    }

    public void update(Report report) {
        reportMapper.updateById(report);
    }

    public String generateReport(String topic, String requirements) {
        String apiKey = configService.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return generateFallbackReport(topic, requirements);
        }

        try {
            String apiUrl = configService.getApiUrl();
            String model = configService.getModel();
            String prompt = buildPrompt(topic, requirements);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一位专业的技术分析专家，擅长撰写技术分析报告。请使用Markdown格式输出。");
            messages.add(systemMsg);
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 8192);
            requestBody.put("stream", false);

            log.info("调用智谱API: {}, 模型: {}", apiUrl, model);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            log.info("智谱API响应状态: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("成功获取AI生成内容，长度: {}", content.length());
                    return content;
                }
            }
            log.warn("API响应格式异常: {}", response.getBody());
            return generateFallbackReport(topic, requirements);
        } catch (Exception e) {
            log.error("调用大模型API失败: {}", e.getMessage(), e);
            return generateFallbackReport(topic, requirements);
        }

        
    }

    private String buildPrompt(String topic, String requirements) {
        if (promptTemplate == null) {
            throw new IllegalStateException("提示词模板未加载，请检查 templates/report-prompt.template 文件");
        }
        
        String prompt = promptTemplate.replace("${topic}", topic);
        
        if (requirements != null && !requirements.isEmpty()) {
            prompt = prompt.replace("${requirements}", "【具体要求】" + requirements + "\n\n");
        } else {
            prompt = prompt.replace("${requirements}", "");
        }
        
        return prompt;
    }

    private String generateFallbackReport(String topic, String requirements) {
        if (fallbackTemplate == null) {
            throw new IllegalStateException("回退模板未加载，请检查 templates/report-fallback.template 文件");
        }
        
        return fallbackTemplate.replace("${topic}", topic);
    }
}