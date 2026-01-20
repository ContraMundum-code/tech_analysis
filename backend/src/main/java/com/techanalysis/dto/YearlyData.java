package com.techanalysis.dto;

public class YearlyData {
    private Integer year;
    private Long patentCount;
    private Long projectCount;
    private Long paperCount;

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Long getPatentCount() { return patentCount; }
    public void setPatentCount(Long patentCount) { this.patentCount = patentCount; }
    public Long getProjectCount() { return projectCount; }
    public void setProjectCount(Long projectCount) { this.projectCount = projectCount; }
    public Long getPaperCount() { return paperCount; }
    public void setPaperCount(Long paperCount) { this.paperCount = paperCount; }
}
