package com.techanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechAnalysisPlatformApplication {
    public static void main(String[] args) {
        // 设置 XML 解析器安全限制，支持大文件解析
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.elementAttributeLimit", "0");
        System.setProperty("jdk.xml.maxOccurLimit", "0");
        System.setProperty("jdk.xml.entityReplacementLimit", "0");
        
        SpringApplication.run(TechAnalysisPlatformApplication.class, args);
    }
}
