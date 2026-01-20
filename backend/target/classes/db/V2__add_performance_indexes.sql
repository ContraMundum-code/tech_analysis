-- ============================================================
-- 性能优化索引迁移脚本
-- 版本: V2
-- 说明: 为常用查询字段添加索引以提升检索速度
-- 执行方式: 在 MySQL 中手动执行，或通过 Flyway/Liquibase 等迁移工具
-- ============================================================

USE tech_analysis;

-- ========== 专利表 (patent) 索引优化 ==========
-- 全文检索用索引 (对 LIKE '%keyword%' 无直接帮助，但可加速 = 和前缀匹配)
CREATE INDEX idx_patent_title_keywords ON patent(title_keywords(100));
CREATE INDEX idx_patent_abstract_keywords ON patent(abstract_keywords(100));
-- 申请日期排序索引
CREATE INDEX idx_patent_application_date ON patent(application_date);
-- 专利有效性筛选
CREATE INDEX idx_patent_validity ON patent(validity);
-- 分类号检索 (前缀索引)
CREATE INDEX idx_patent_classification ON patent(classification_code(50));
-- 复合索引: 国家+年份 (常见筛选组合)
CREATE INDEX idx_patent_country_year ON patent(country, publication_year);

-- ========== 论文表 (paper) 索引优化 ==========
CREATE INDEX idx_paper_title_keywords ON paper(title_keywords(100));
CREATE INDEX idx_paper_abstract_keywords ON paper(abstract_keywords(100));
-- DOI 唯一索引 (去重 & 快速定位)
CREATE UNIQUE INDEX idx_paper_doi ON paper(doi);
-- 引用次数排序
CREATE INDEX idx_paper_citation ON paper(citation_count);
-- 复合索引: 国家+年份
CREATE INDEX idx_paper_country_year ON paper(country(100), publication_year);
-- 期刊检索
CREATE INDEX idx_paper_journal ON paper(journal(100));

-- ========== 项目表 (project) 索引优化 ==========
CREATE INDEX idx_project_title_keywords ON project(title_keywords(100));
CREATE INDEX idx_project_abstract_keywords ON project(abstract_keywords(100));
-- 项目编号唯一索引
CREATE UNIQUE INDEX idx_project_id ON project(project_id);
-- 承担机构检索
CREATE INDEX idx_project_institution ON project(institution(100));
-- 项目类型筛选
CREATE INDEX idx_project_type ON project(project_type);
-- 资助金额排序
CREATE INDEX idx_project_funding ON project(funding_amount);
-- 复合索引: 资助机构+年份
CREATE INDEX idx_project_fundingorg_year ON project(funding_org(100), project_year);
-- 国家+年份 (若有国际项目对比需求)
CREATE INDEX idx_project_country_year ON project(country, project_year);

-- ========== 关键词表 (keyword) ==========
-- 已有唯一键，此处可选加单独索引加速模糊匹配
CREATE INDEX idx_keyword_cn ON keyword(keyword_cn);
CREATE INDEX idx_keyword_en ON keyword(keyword_en);

-- ========== 用户表 (user) ==========
-- 邮箱检索
CREATE INDEX idx_user_email ON user(email);

-- ========== 报告表 (report) ==========
-- 按状态筛选
CREATE INDEX idx_report_status ON report(status);
-- 用户+状态复合
CREATE INDEX idx_report_user_status ON report(user_id, status);

-- ============================================================
-- 执行完毕后可通过 EXPLAIN 验证查询是否命中索引
-- 示例: EXPLAIN SELECT * FROM patent WHERE country = '中国' AND publication_year = 2024;
-- ============================================================
