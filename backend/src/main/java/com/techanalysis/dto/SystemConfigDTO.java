package com.techanalysis.dto;

public class SystemConfigDTO {
    private String apiUrl;
    private String apiKey;
    private String model;

    public SystemConfigDTO() {}

    public SystemConfigDTO(String apiUrl, String apiKey, String model) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
