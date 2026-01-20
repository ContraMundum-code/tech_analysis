package com.techanalysis.dto;

public class NetworkLink {
    private String source;
    private String target;
    private Integer value; // 关系强度（合作次数）

    public NetworkLink() {}

    public NetworkLink(String source, String target) {
        this.source = source;
        this.target = target;
        this.value = 1;
    }
    
    public NetworkLink(String source, String target, Integer value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}
