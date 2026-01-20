package com.techanalysis.dto;

import java.util.List;

public class CategoryData {
    private List<TechCategory> categories;

    public CategoryData() {}

    public List<TechCategory> getCategories() { return categories; }
    public void setCategories(List<TechCategory> categories) { this.categories = categories; }

    public static class TechCategory {
        private String code;
        private String name;
        private String enName;
        private String description;
        private String icon;
        private long patentCount;
        private long paperCount;
        private long projectCount;
        private long totalCount;
        private List<TechSubCategory> subItems;

        public TechCategory() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEnName() { return enName; }
        public void setEnName(String enName) { this.enName = enName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public long getPatentCount() { return patentCount; }
        public void setPatentCount(long patentCount) { this.patentCount = patentCount; }
        public long getPaperCount() { return paperCount; }
        public void setPaperCount(long paperCount) { this.paperCount = paperCount; }
        public long getProjectCount() { return projectCount; }
        public void setProjectCount(long projectCount) { this.projectCount = projectCount; }
        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
        public List<TechSubCategory> getSubItems() { return subItems; }
        public void setSubItems(List<TechSubCategory> subItems) { this.subItems = subItems; }
    }

    public static class TechSubCategory {
        private String code;
        private String name;
        private String enName;
        private String description;
        private String icon;
        private long count;
        private List<String> keywords;

        public TechSubCategory() {}

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEnName() { return enName; }
        public void setEnName(String enName) { this.enName = enName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    }
}
