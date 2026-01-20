package com.techanalysis.dto;

public class NetworkNode {
    private String id;
    private String name;
    private String category;
    private Integer value;

    public NetworkNode() {}

    public NetworkNode(String id, String name, String category, Integer value) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.value = value;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}
