package com.techanalysis.dto;

import java.util.List;

public class HotTechData {
    private List<TechRank> rankings;

    public List<TechRank> getRankings() { return rankings; }
    public void setRankings(List<TechRank> rankings) { this.rankings = rankings; }

    public static class TechRank {
        private String name;
        private long count;
        private double growthRate; // 增长率

        public TechRank() {}
        public TechRank(String name, long count, double growthRate) {
            this.name = name;
            this.count = count;
            this.growthRate = growthRate;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public double getGrowthRate() { return growthRate; }
        public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
    }
}
