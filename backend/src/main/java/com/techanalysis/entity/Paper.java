package com.techanalysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String authors;           // Authors
    private String authorFullNames;   // Author Full Names
    private String title;             // 标题
    private String sourceTitle;       // Source Title
    private String documentType;      // Document Type
    private String authorKeywords;    // Author Keywords
    @TableField("abstract_text")
    private String abstractText;      // 摘要
    private String addresses;         // Addresses
    private String doi;               // DOI
    private String wosCategories;     // WoS Categories
    private String researchAreas;     // Research Areas
    private String country;           // Countries
    private Integer citationCount;    // Times Cited
    private Integer publicationYear;  // Publication Year
    private String journal;           // Journal (Source Title)
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
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getAuthorFullNames() { return authorFullNames; }
    public void setAuthorFullNames(String authorFullNames) { this.authorFullNames = authorFullNames; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getAuthorKeywords() { return authorKeywords; }
    public void setAuthorKeywords(String authorKeywords) { this.authorKeywords = authorKeywords; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public String getAddresses() { return addresses; }
    public void setAddresses(String addresses) { this.addresses = addresses; }
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    public String getWosCategories() { return wosCategories; }
    public void setWosCategories(String wosCategories) { this.wosCategories = wosCategories; }
    public String getResearchAreas() { return researchAreas; }
    public void setResearchAreas(String researchAreas) { this.researchAreas = researchAreas; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Integer getCitationCount() { return citationCount; }
    public void setCitationCount(Integer citationCount) { this.citationCount = citationCount; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
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
