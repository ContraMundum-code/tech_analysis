package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techanalysis.dto.SystemConfigDTO;
import com.techanalysis.entity.SystemConfig;
import com.techanalysis.repository.SystemConfigMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemConfigService {

    private static final String KEY_API_URL = "llm_api_url";
    private static final String KEY_API_KEY = "llm_api_key";
    private static final String KEY_MODEL = "llm_model";

    private final SystemConfigMapper configMapper;

    public SystemConfigService(SystemConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    public SystemConfigDTO getConfig() {
        String apiUrl = getValue(KEY_API_URL, "https://open.bigmodel.cn/api/paas/v4/chat/completions");
        String apiKey = getValue(KEY_API_KEY, "");
        String model = getValue(KEY_MODEL, "glm-4-flash");
        
        // 隐藏 API Key 中间部分
        String maskedKey = maskApiKey(apiKey);
        return new SystemConfigDTO(apiUrl, maskedKey, model);
    }

    public void saveConfig(SystemConfigDTO dto) {
        saveValue(KEY_API_URL, dto.getApiUrl(), "大模型API地址");
        // 只有当 apiKey 不是掩码时才更新
        if (dto.getApiKey() != null && !dto.getApiKey().contains("***")) {
            saveValue(KEY_API_KEY, dto.getApiKey(), "大模型API密钥");
        }
        saveValue(KEY_MODEL, dto.getModel(), "大模型名称");
    }

    public String getApiUrl() {
        return getValue(KEY_API_URL, "https://open.bigmodel.cn/api/paas/v4/chat/completions");
    }

    public String getApiKey() {
        return getValue(KEY_API_KEY, "");
    }

    public String getModel() {
        return getValue(KEY_MODEL, "glm-4-flash");
    }

    private String getValue(String key, String defaultValue) {
        SystemConfig config = configMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        return config != null ? config.getConfigValue() : defaultValue;
    }

    private void saveValue(String key, String value, String description) {
        SystemConfig config = configMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        if (config == null) {
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setDescription(description);
            config.setUpdatedAt(LocalDateTime.now());
            configMapper.insert(config);
        } else {
            config.setConfigValue(value);
            config.setUpdatedAt(LocalDateTime.now());
            configMapper.updateById(config);
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return apiKey;
        }
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}
