package com.techanalysis.dto;

public class TechMaturityData {
    private long emergingCount;    // 新兴技术（近2年出现）
    private long growingCount;     // 成长技术（3-5年）
    private long matureCount;      // 成熟技术（5年以上）
    private double emergingPercent;
    private double growingPercent;
    private double maturePercent;

    public long getEmergingCount() { return emergingCount; }
    public void setEmergingCount(long emergingCount) { this.emergingCount = emergingCount; }
    public long getGrowingCount() { return growingCount; }
    public void setGrowingCount(long growingCount) { this.growingCount = growingCount; }
    public long getMatureCount() { return matureCount; }
    public void setMatureCount(long matureCount) { this.matureCount = matureCount; }
    public double getEmergingPercent() { return emergingPercent; }
    public void setEmergingPercent(double emergingPercent) { this.emergingPercent = emergingPercent; }
    public double getGrowingPercent() { return growingPercent; }
    public void setGrowingPercent(double growingPercent) { this.growingPercent = growingPercent; }
    public double getMaturePercent() { return maturePercent; }
    public void setMaturePercent(double maturePercent) { this.maturePercent = maturePercent; }
}
