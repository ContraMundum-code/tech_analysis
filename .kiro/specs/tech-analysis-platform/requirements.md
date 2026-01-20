# Requirements Document

## Introduction

本系统是一个技术解析平台，旨在对给定技术领域进行细粒度分析，包括技术内容、技术布局、技术研发和技术生态。系统通过采集专利、论文和项目数据，构建领域技术知识库，分析国内外竞争态势，对比技术差异，识别我国科技优势及短板，为技术研发提供建议和数据支撑。

技术栈：Vue 3 + Spring Boot 3 + MySQL + ECharts（图表可视化）

## Glossary

- **Tech_Analysis_Platform**: 技术解析平台，系统的整体名称
- **Data_Collector**: 数据采集模块，负责采集专利、论文和项目数据
- **Knowledge_Base**: 技术知识库，存储解析后的技术要素和关联关系
- **Patent**: 专利数据实体，包含专利号、标题、摘要、申请人、分类号等
- **Paper**: 论文数据实体，包含标题、作者、摘要、关键词、引用数等
- **Project**: 项目数据实体，包含项目名称、负责人、资助金额、执行机构等
- **Tech_Map**: 技术图谱，可视化展示技术分布和关联关系
- **Trend_Analyzer**: 趋势分析模块，分析技术发展时序趋势
- **Report_Generator**: 报告生成模块，基于LLM生成分析报告
- **User**: 系统用户，包括普通用户和管理员
- **Admin**: 管理员用户，具有数据导入和系统管理权限

## Requirements

### Requirement 1: 用户认证与授权

**User Story:** As a user, I want to register and login to the system, so that I can access the analysis features and manage my data.

#### Acceptance Criteria

1. WHEN a user submits valid registration information THEN THE Tech_Analysis_Platform SHALL create a new user account and send confirmation
2. WHEN a user provides correct credentials THEN THE Tech_Analysis_Platform SHALL authenticate the user and create a session
3. WHEN a user provides incorrect credentials THEN THE Tech_Analysis_Platform SHALL reject the login attempt and display an error message
4. WHILE a user is not authenticated THEN THE Tech_Analysis_Platform SHALL restrict access to protected features
5. WHEN an admin user logs in THEN THE Tech_Analysis_Platform SHALL grant access to data import and management features

### Requirement 2: 数据导入管理

**User Story:** As an admin, I want to import patent, paper and project data from Excel files, so that I can populate the knowledge base for analysis.

#### Acceptance Criteria

1. WHEN an admin uploads an Excel file containing patent data THEN THE Data_Collector SHALL parse the file and extract patent records
2. WHEN an admin uploads an Excel file containing project data THEN THE Data_Collector SHALL parse the file and extract project records
3. WHEN an admin uploads an Excel file containing paper data THEN THE Data_Collector SHALL parse the file and extract paper records
4. WHEN parsing an Excel file THEN THE Data_Collector SHALL validate data format and report any errors
5. IF an uploaded file contains invalid format THEN THE Data_Collector SHALL reject the file and display specific error messages
6. WHEN data is successfully parsed THEN THE Data_Collector SHALL display a preview of the data before saving
7. WHEN an admin confirms data import THEN THE Data_Collector SHALL persist the data to the Knowledge_Base
8. THE Data_Collector SHALL support configurable options including header row detection, empty row skipping, and whitespace trimming

### Requirement 3: 专利数据解析与存储

**User Story:** As a system, I want to parse and store patent data with structured fields, so that I can support downstream analysis.

#### Acceptance Criteria

1. WHEN storing a patent record THEN THE Knowledge_Base SHALL extract and store: patent number, title, abstract, applicant, inventor, classification code, application date, publication date, and country
2. WHEN parsing patent data THEN THE Knowledge_Base SHALL extract Chinese and English entity keywords
3. THE Knowledge_Base SHALL serialize patent records to JSON format for storage
4. THE Knowledge_Base SHALL deserialize JSON data back to patent objects for retrieval
5. FOR ALL valid patent objects, serializing then deserializing SHALL produce an equivalent object (round-trip property)

### Requirement 4: 项目数据解析与存储

**User Story:** As a system, I want to parse and store project data with structured fields, so that I can support funding and institution analysis.

#### Acceptance Criteria

1. WHEN storing a project record THEN THE Knowledge_Base SHALL extract and store: project name, principal investigator, funding amount, institution, start date, end date, and country
2. WHEN parsing project data THEN THE Knowledge_Base SHALL extract Chinese and English entity keywords
3. THE Knowledge_Base SHALL serialize project records to JSON format for storage
4. THE Knowledge_Base SHALL deserialize JSON data back to project objects for retrieval
5. FOR ALL valid project objects, serializing then deserializing SHALL produce an equivalent object (round-trip property)

### Requirement 5: 技术图谱可视化

**User Story:** As a user, I want to view an interactive technology map showing global distribution, so that I can understand technology landscape across countries.

#### Acceptance Criteria

1. WHEN a user visits the tech map page THEN THE Tech_Map SHALL display a world map using ECharts geo component with country-level technology distribution
2. WHEN a user hovers over a country THEN THE Tech_Map SHALL display detailed statistics including project count, patent count, and funding amount via ECharts tooltip
3. WHEN a user selects a data type (project count, funding amount, tech count) THEN THE Tech_Map SHALL update the ECharts visualization accordingly
4. WHEN a user clicks on a country THEN THE Tech_Map SHALL display a detailed analysis panel with ECharts bar charts
5. THE Tech_Map SHALL support zoom and pan interactions using ECharts roam feature for map exploration
6. WHEN displaying country data THEN THE Tech_Map SHALL show ECharts bar charts comparing metrics across time periods

### Requirement 6: 竞争态势分析

**User Story:** As a user, I want to analyze competitive landscape of domestic and international players, so that I can understand technology positioning.

#### Acceptance Criteria

1. WHEN a user requests competitive analysis THEN THE Trend_Analyzer SHALL display patent application trends by country over time
2. WHEN a user selects a country THEN THE Trend_Analyzer SHALL show top institutions and their patent/project counts
3. WHEN analyzing competition THEN THE Trend_Analyzer SHALL compare China's position against major technology powers (USA, Japan, South Korea, Germany)
4. THE Trend_Analyzer SHALL identify and highlight technology leaders in each sub-domain
5. WHEN displaying competitive data THEN THE Trend_Analyzer SHALL show year-over-year growth rates

### Requirement 7: 技术趋势分析

**User Story:** As a user, I want to analyze technology development trends over time, so that I can identify emerging technologies and predict future directions.

#### Acceptance Criteria

1. WHEN a user visits the trend analysis page THEN THE Trend_Analyzer SHALL display ECharts line charts for time-series technology metrics
2. WHEN a user selects a time range THEN THE Trend_Analyzer SHALL filter data and update ECharts charts to the specified period
3. WHEN a user selects a country filter THEN THE Trend_Analyzer SHALL show trends for that specific country using ECharts
4. THE Trend_Analyzer SHALL display hot technology rankings with growth percentages using ECharts bar charts
5. THE Trend_Analyzer SHALL show technology maturity distribution (emerging, growing, mature) using ECharts pie charts
6. THE Trend_Analyzer SHALL display regional distribution analysis using ECharts visualization

### Requirement 8: 国内外技术对比

**User Story:** As a user, I want to compare domestic and international technology capabilities, so that I can identify China's strengths and weaknesses.

#### Acceptance Criteria

1. WHEN a user requests technology comparison THEN THE Tech_Analysis_Platform SHALL display side-by-side comparison of China vs other countries
2. THE Tech_Analysis_Platform SHALL compare research topics, research scope, and research content between countries
3. THE Tech_Analysis_Platform SHALL identify technology areas where China leads
4. THE Tech_Analysis_Platform SHALL identify technology gaps where China lags behind
5. WHEN displaying comparison results THEN THE Tech_Analysis_Platform SHALL provide quantitative metrics and qualitative insights

### Requirement 9: 分析报告生成

**User Story:** As a user, I want to generate comprehensive analysis reports, so that I can share insights and support decision making.

#### Acceptance Criteria

1. WHEN a user clicks generate report THEN THE Report_Generator SHALL create a structured analysis report
2. THE Report_Generator SHALL integrate with LLM to generate narrative analysis based on data
3. THE Report_Generator SHALL include sections: executive summary, technology overview, competitive analysis, trend analysis, and recommendations
4. WHEN generating a report THEN THE Report_Generator SHALL display real-time progress with streaming output
5. THE Report_Generator SHALL support customizable report templates
6. WHEN a report is generated THEN THE Report_Generator SHALL allow download in multiple formats (PDF, Word)
7. THE Report_Generator SHALL serialize report content to JSON for storage
8. THE Report_Generator SHALL deserialize JSON data back to report objects for display

### Requirement 10: 三层技术关联图谱

**User Story:** As a user, I want to explore technology relationships through a three-layer network graph, so that I can understand technology dependencies and connections.

#### Acceptance Criteria

1. WHEN a user visits the three-layer graph page THEN THE Tech_Map SHALL display a hierarchical network visualization using ECharts graph component
2. THE Tech_Map SHALL show three layers: technology domains, sub-technologies, and keywords using ECharts force-directed layout
3. WHEN a user clicks on a node THEN THE Tech_Map SHALL highlight connected nodes using ECharts emphasis and display details via tooltip
4. THE Tech_Map SHALL support filtering by technology category with ECharts legend interaction
5. THE Tech_Map SHALL support search functionality to locate specific technologies and focus the ECharts view

### Requirement 11: 数据搜索与筛选

**User Story:** As a user, I want to search and filter technology data, so that I can find specific information quickly.

#### Acceptance Criteria

1. WHEN a user enters a search query THEN THE Tech_Analysis_Platform SHALL return matching patents, papers, and projects
2. THE Tech_Analysis_Platform SHALL support filtering by: country, time period, technology category, and institution
3. WHEN displaying search results THEN THE Tech_Analysis_Platform SHALL show relevance scores and highlight matching terms
4. THE Tech_Analysis_Platform SHALL support advanced search with boolean operators
5. WHEN no results are found THEN THE Tech_Analysis_Platform SHALL suggest alternative search terms

### Requirement 12: 系统性能与可靠性

**User Story:** As a system administrator, I want the system to perform reliably under load, so that users have a consistent experience.

#### Acceptance Criteria

1. THE Tech_Analysis_Platform SHALL respond to user requests within 3 seconds under normal load
2. THE Tech_Analysis_Platform SHALL support concurrent access by at least 100 users
3. WHEN database operations fail THEN THE Tech_Analysis_Platform SHALL log errors and display user-friendly messages
4. THE Tech_Analysis_Platform SHALL implement data backup and recovery mechanisms
5. WHEN the system encounters errors THEN THE Tech_Analysis_Platform SHALL gracefully degrade functionality rather than crash
