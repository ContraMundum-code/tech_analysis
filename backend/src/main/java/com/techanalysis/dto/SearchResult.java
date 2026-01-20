package com.techanalysis.dto;

import java.util.List;

public class SearchResult {
    private List<SearchItem> patents;
    private List<SearchItem> papers;
    private List<SearchItem> projects;
    private int totalCount;

    public SearchResult() {}

    public List<SearchItem> getPatents() { return patents; }
    public void setPatents(List<SearchItem> patents) { this.patents = patents; }
    public List<SearchItem> getPapers() { return papers; }
    public void setPapers(List<SearchItem> papers) { this.papers = papers; }
    public List<SearchItem> getProjects() { return projects; }
    public void setProjects(List<SearchItem> projects) { this.projects = projects; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public static class SearchItem {
        private Long id;
        private String title;
        private String type;
        private String country;
        private Integer year;
        private String keywords;

        public SearchItem() {}
        public SearchItem(Long id, String title, String type, String country, Integer year, String keywords) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.country = country;
            this.year = year;
            this.keywords = keywords;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public String getKeywords() { return keywords; }
        public void setKeywords(String keywords) { this.keywords = keywords; }
    }
}
