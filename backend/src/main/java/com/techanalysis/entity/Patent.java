package com.techanalysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@TableName("patent")
public class Patent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String patentNumber;      // 公开号
    private String title;             // 标题
    @TableField("abstract_text")
    private String abstractText;      // 摘要
    private String applicant;         // 申请人
    private String inventor;          // 发明人（暂不使用）
    private String classificationCode; // IPC分类号
    private String country;           // 公开国别
    private LocalDate applicationDate; // 申请日期（暂不使用）
    private LocalDate publicationDate; // 公开日期
    private Integer publicationYear;  // 公开年份
    private Integer publicationMonth; // 公开月份
    private String validity;          // 专利有效性
    private String titleKeywords;     // 标题关键词
    private String abstractKeywords;  // 摘要关键词
    private String titleEntities;     // 标题实体
    private String abstractEntities;  // 摘要实体
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(exist = false)
    private List<Keyword> keywords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPatentNumber() { return patentNumber; }
    public void setPatentNumber(String patentNumber) { this.patentNumber = patentNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }
    public String getInventor() { return inventor; }
    public void setInventor(String inventor) { this.inventor = inventor; }
    public String getClassificationCode() { return classificationCode; }
    public void setClassificationCode(String classificationCode) { this.classificationCode = classificationCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public Integer getPublicationMonth() { return publicationMonth; }
    public void setPublicationMonth(Integer publicationMonth) { this.publicationMonth = publicationMonth; }
    public String getValidity() { return validity; }
    public void setValidity(String validity) { this.validity = validity; }
    public String getTitleKeywords() { return titleKeywords; }
    public void setTitleKeywords(String titleKeywords) { this.titleKeywords = titleKeywords; }
    public String getAbstractKeywords() { return abstractKeywords; }
    public void setAbstractKeywords(String abstractKeywords) { this.abstractKeywords = abstractKeywords; }
    public String getTitleEntities() { return titleEntities; }
    public void setTitleEntities(String titleEntities) { this.titleEntities = titleEntities; }
    public String getAbstractEntities() { return abstractEntities; }
    public void setAbstractEntities(String abstractEntities) { this.abstractEntities = abstractEntities; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Keyword> getKeywords() { return keywords; }
    public void setKeywords(List<Keyword> keywords) { this.keywords = keywords; }
}
