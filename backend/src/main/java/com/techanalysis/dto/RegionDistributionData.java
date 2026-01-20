package com.techanalysis.dto;

import java.util.List;

public class RegionDistributionData {
    private List<RegionData> regions;

    public List<RegionData> getRegions() { return regions; }
    public void setRegions(List<RegionData> regions) { this.regions = regions; }

    public static class RegionData {
        private String region;
        private long count;
        private double percent;

        public RegionData() {}
        public RegionData(String region, long count, double percent) {
            this.region = region;
            this.count = count;
            this.percent = percent;
        }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public double getPercent() { return percent; }
        public void setPercent(double percent) { this.percent = percent; }
    }
}
