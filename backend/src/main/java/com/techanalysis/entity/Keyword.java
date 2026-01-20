package com.techanalysis.entity;

import com.baomidou.mybatisplus.annotation.*;

@TableName("keyword")
public class Keyword {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String keywordCn;
    private String keywordEn;
    private String category;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeywordCn() { return keywordCn; }
    public void setKeywordCn(String keywordCn) { this.keywordCn = keywordCn; }
    public String getKeywordEn() { return keywordEn; }
    public void setKeywordEn(String keywordEn) { this.keywordEn = keywordEn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
