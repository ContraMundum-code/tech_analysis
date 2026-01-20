package com.techanalysis.dto;

import java.util.List;

public class CompetitionData {
    private List<CountryCompetition> competitions;
    private Long totalProjectCount; // 项目总数（项目没有国家字段）

    public List<CountryCompetition> getCompetitions() { return competitions; }
    public void setCompetitions(List<CountryCompetition> competitions) { this.competitions = competitions; }
    public Long getTotalProjectCount() { return totalProjectCount; }
    public void setTotalProjectCount(Long totalProjectCount) { this.totalProjectCount = totalProjectCount; }
}
