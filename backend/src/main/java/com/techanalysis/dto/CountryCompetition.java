package com.techanalysis.dto;

public class CountryCompetition {
    private String country;
    private Long patentCount;
    private Long projectCount;
    private Long paperCount;

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Long getPatentCount() { return patentCount; }
    public void setPatentCount(Long patentCount) { this.patentCount = patentCount; }
    public Long getProjectCount() { return projectCount; }
    public void setProjectCount(Long projectCount) { this.projectCount = projectCount; }
    public Long getPaperCount() { return paperCount; }
    public void setPaperCount(Long paperCount) { this.paperCount = paperCount; }
}
