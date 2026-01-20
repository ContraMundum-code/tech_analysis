package com.techanalysis.config;

import jakarta.annotation.PostConstruct;
import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PoiConfig {

    @PostConstruct
    public void init() {
        // 设置 POI 最大字节数组大小为 500MB，支持大文件
        IOUtils.setByteArrayMaxOverride(500 * 1024 * 1024);
    }
}
