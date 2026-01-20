-- 技术解析平台数据库初始化脚本
CREATE DATABASE IF NOT EXISTS tech_analysis DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tech_analysis;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100),
    `role` VARCHAR(20) DEFAULT 'USER',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 专利表
CREATE TABLE IF NOT EXISTS `patent` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `patent_number` VARCHAR(100),
    `title` TEXT,
    `abstract_text` LONGTEXT,
    `applicant` TEXT,
    `inventor` TEXT,
    `classification_code` VARCHAR(500),
    `country` VARCHAR(100),
    `application_date` DATE,
    `publication_date` DATE,
    `publication_year` INT,
    `publication_month` INT,
    `validity` VARCHAR(50),
    `title_keywords` TEXT,
    `abstract_keywords` TEXT,
    `title_entities` TEXT,
    `abstract_entities` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_country (country),
    INDEX idx_publication_year (publication_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 项目表
CREATE TABLE IF NOT EXISTS `project` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `project_id` VARCHAR(100),
    `project_name` TEXT,
    `funding_org` TEXT,
    `institution` TEXT,
    `project_type` VARCHAR(100),
    `funding_amount` DECIMAL(15,2),
    `project_year` INT,
    `project_month` INT,
    `abstract_text` LONGTEXT,
    `title_keywords` TEXT,
    `abstract_keywords` TEXT,
    `title_entities` TEXT,
    `abstract_entities` TEXT,
    `principal_investigator` TEXT,
    `country` VARCHAR(100),
    `start_date` DATE,
    `end_date` DATE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_project_year (project_year),
    INDEX idx_funding_org (funding_org(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 论文表
CREATE TABLE IF NOT EXISTS `paper` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `authors` TEXT,
    `author_full_names` TEXT,
    `title` TEXT,
    `source_title` TEXT,
    `document_type` VARCHAR(100),
    `author_keywords` TEXT,
    `abstract_text` LONGTEXT,
    `addresses` TEXT,
    `doi` VARCHAR(255),
    `wos_categories` TEXT,
    `research_areas` TEXT,
    `country` TEXT,
    `citation_count` INT DEFAULT 0,
    `publication_year` INT,
    `journal` TEXT,
    `title_keywords` TEXT,
    `abstract_keywords` TEXT,
    `title_entities` TEXT,
    `abstract_entities` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_country (country(100)),
    INDEX idx_publication_year (publication_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 关键词表
CREATE TABLE IF NOT EXISTS `keyword` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `keyword_cn` VARCHAR(100),
    `keyword_en` VARCHAR(100),
    `category` VARCHAR(50),
    UNIQUE KEY uk_keyword (keyword_cn, keyword_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 专利-关键词关联表
CREATE TABLE IF NOT EXISTS `patent_keyword` (
    `patent_id` BIGINT NOT NULL,
    `keyword_id` BIGINT NOT NULL,
    PRIMARY KEY (patent_id, keyword_id),
    FOREIGN KEY (patent_id) REFERENCES patent(id) ON DELETE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES keyword(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 项目-关键词关联表
CREATE TABLE IF NOT EXISTS `project_keyword` (
    `project_id` BIGINT NOT NULL,
    `keyword_id` BIGINT NOT NULL,
    PRIMARY KEY (project_id, keyword_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES keyword(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 论文-关键词关联表
CREATE TABLE IF NOT EXISTS `paper_keyword` (
    `paper_id` BIGINT NOT NULL,
    `keyword_id` BIGINT NOT NULL,
    PRIMARY KEY (paper_id, keyword_id),
    FOREIGN KEY (paper_id) REFERENCES paper(id) ON DELETE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES keyword(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 报告表
CREATE TABLE IF NOT EXISTS `report` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT,
    `title` VARCHAR(255),
    `content` LONGTEXT,
    `status` VARCHAR(20) DEFAULT 'DRAFT',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `config_key` VARCHAR(100) NOT NULL UNIQUE,
    `config_value` TEXT,
    `description` VARCHAR(255),
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认配置
INSERT INTO `system_config` (config_key, config_value, description) VALUES 
('llm_api_url', 'https://open.bigmodel.cn/api/paas/v4/chat/completions', '大模型API地址'),
('llm_api_key', '', '大模型API密钥'),
('llm_model', 'glm-4-flash', '大模型名称')
ON DUPLICATE KEY UPDATE config_key=config_key;

-- 插入默认管理员用户 (密码: admin123)
-- 插入默认普通用户 (密码: user123)
INSERT INTO `user` (username, password_hash, email, role) VALUES 
('admin', '$2a$10$EqKcp1WFKVQISheBxkV.qOXEHGBFjLNiSiJBGJHPMFTqBqPJHcwKa', 'admin@example.com', 'ADMIN'),
('user', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQbLgtnOoKsWc/XGKHYku3UAQZ5Gne', 'user@example.com', 'USER')
ON DUPLICATE KEY UPDATE username=username;
