package com.techanalysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@TableName("project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectId;           // 项目id
    private String projectName;         // 标题
    private String fundingOrg;          // 资助机构
    private String institution;         // 承担机构
    private String projectType;         // 项目类型
    private BigDecimal fundingAmount;   // 项目金额
    private Integer projectYear;        // 项目年份
    private Integer projectMonth;       // 项目月份
    @TableField("abstract_text")
    private String abstractText;        // 摘要
    private String titleKeywords;       // 标题关键词
    private String abstractKeywords;    // 摘要关键词
    private String titleEntities;       // 标题实体
    private String abstractEntities;    // 摘要实体
    private String principalInvestigator; // 负责人（暂不使用）
    private String country;             // 国家（暂不使用）
    private LocalDate startDate;        // 开始日期
    private LocalDate endDate;          // 结束日期
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(exist = false)
    private List<Keyword> keywords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getFundingOrg() { return fundingOrg; }
    public void setFundingOrg(String fundingOrg) { this.fundingOrg = fundingOrg; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    public BigDecimal getFundingAmount() { return fundingAmount; }
    public void setFundingAmount(BigDecimal fundingAmount) { this.fundingAmount = fundingAmount; }
    public Integer getProjectYear() { return projectYear; }
    public void setProjectYear(Integer projectYear) { this.projectYear = projectYear; }
    public Integer getProjectMonth() { return projectMonth; }
    public void setProjectMonth(Integer projectMonth) { this.projectMonth = projectMonth; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public String getTitleKeywords() { return titleKeywords; }
    public void setTitleKeywords(String titleKeywords) { this.titleKeywords = titleKeywords; }
    public String getAbstractKeywords() { return abstractKeywords; }
    public void setAbstractKeywords(String abstractKeywords) { this.abstractKeywords = abstractKeywords; }
    public String getTitleEntities() { return titleEntities; }
    public void setTitleEntities(String titleEntities) { this.titleEntities = titleEntities; }
    public String getAbstractEntities() { return abstractEntities; }
    public void setAbstractEntities(String abstractEntities) { this.abstractEntities = abstractEntities; }
    public String getPrincipalInvestigator() { return principalInvestigator; }
    public void setPrincipalInvestigator(String principalInvestigator) { this.principalInvestigator = principalInvestigator; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Keyword> getKeywords() { return keywords; }
    public void setKeywords(List<Keyword> keywords) { this.keywords = keywords; }
}
