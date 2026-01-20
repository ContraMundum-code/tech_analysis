package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techanalysis.dto.*;
import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import com.techanalysis.repository.PaperMapper;
import com.techanalysis.repository.PatentMapper;
import com.techanalysis.repository.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    // 分类代码对应的关键词映射
    // 注意：TP181（计算机视觉）不设置关键词，因为数据库中所有数据都是该领域的
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();
    static {
        CATEGORY_KEYWORDS.put("TP18", Arrays.asList("人工智能", "机器学习", "深度学习", "神经网络", "AI"));
        // TP181 不过滤，显示所有数据（数据库中的数据都是计算机视觉领域的）
        CATEGORY_KEYWORDS.put("TP181", Collections.emptyList());
        CATEGORY_KEYWORDS.put("TP182", Arrays.asList("自然语言", "NLP", "文本分析", "机器翻译"));
        CATEGORY_KEYWORDS.put("TP183", Arrays.asList("机器学习", "监督学习", "无监督学习", "强化学习"));
        CATEGORY_KEYWORDS.put("TP184", Arrays.asList("知识图谱", "知识表示", "逻辑推理"));
        CATEGORY_KEYWORDS.put("TP3", Arrays.asList("计算机", "软件", "算法", "数据结构", "程序"));
        CATEGORY_KEYWORDS.put("TP2", Arrays.asList("自动化", "控制", "机器人", "PLC", "传感器"));
        CATEGORY_KEYWORDS.put("TN", Arrays.asList("电子", "电路", "半导体", "芯片", "集成电路"));
        CATEGORY_KEYWORDS.put("TN91", Arrays.asList("通信", "无线", "5G", "4G", "网络", "信号"));
    }

    private final PatentMapper patentMapper;
    private final ProjectMapper projectMapper;
    private final PaperMapper paperMapper;
    private final ObjectMapper objectMapper;

    public AnalysisService(PatentMapper patentMapper, ProjectMapper projectMapper, PaperMapper paperMapper,
                          ObjectMapper objectMapper) {
        this.patentMapper = patentMapper;
        this.projectMapper = projectMapper;
        this.paperMapper = paperMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取分类对应的关键词列表
     */
    private List<String> getCategoryKeywords(String categoryCode) {
        if (categoryCode == null || categoryCode.isEmpty()) {
            return Collections.emptyList();
        }
        return CATEGORY_KEYWORDS.getOrDefault(categoryCode, Collections.emptyList());
    }

    /**
     * 从数据库动态获取所有国家
     */
    private Set<String> getAllCountries() {
        Set<String> countries = new LinkedHashSet<>();
        
        List<Patent> patents = patentMapper.selectList(
            new QueryWrapper<Patent>().select("DISTINCT country").isNotNull("country").ne("country", ""));
        patents.forEach(p -> {
            if (p.getCountry() != null && !p.getCountry().trim().isEmpty()) {
                countries.add(p.getCountry().trim());
            }
        });
        
        List<Project> projects = projectMapper.selectList(
            new QueryWrapper<Project>().select("DISTINCT country").isNotNull("country").ne("country", ""));
        projects.forEach(p -> {
            if (p.getCountry() != null && !p.getCountry().trim().isEmpty()) {
                countries.add(p.getCountry().trim());
            }
        });
        
        List<Paper> papers = paperMapper.selectList(
            new QueryWrapper<Paper>().select("DISTINCT country").isNotNull("country").ne("country", ""));
        papers.forEach(p -> {
            if (p.getCountry() != null && !p.getCountry().trim().isEmpty()) {
                countries.add(p.getCountry().trim());
            }
        });
        
        log.info("数据库中的国家列表: {}", countries);
        return countries;
    }

    public TechMapData getTechMapData(String dataType, String categoryCode) {
        log.info("查询 TechMap 数据, dataType={}, categoryCode={}", dataType, categoryCode);
        return queryTechMapFromDB(dataType, categoryCode);
    }
    
    private TechMapData queryTechMapFromDB(String dataType, String categoryCode) {
        TechMapData data = new TechMapData();
        List<CountryData> countryDataList = new ArrayList<>();
        List<String> keywords = getCategoryKeywords(categoryCode);
        
        if ("project".equals(dataType)) {
            List<CountryData> fundingOrgData = getFundingOrgData(keywords);
            data.setCountries(fundingOrgData);
            log.info("TechMap 项目数据（按资助机构）: {} 个机构, categoryCode={}", fundingOrgData.size(), categoryCode);
            return data;
        }
        
        Set<String> countries = getAllCountries();
        if (countries.isEmpty()) {
            countries = new LinkedHashSet<>(Arrays.asList("中国", "美国", "日本", "德国", "韩国", "英国", "法国"));
        }
        
        for (String country : countries) {
            CountryData cd = new CountryData();
            cd.setCountry(country);
            switch (dataType) {
                case "patent" -> cd.setCount(countPatentsByCountryAndKeywords(country, keywords));
                case "paper" -> cd.setCount(countPapersByCountryAndKeywords(country, keywords));
                default -> cd.setCount(countPatentsByCountryAndKeywords(country, keywords));
            }
            if (cd.getCount() > 0) {
                countryDataList.add(cd);
            }
        }
        
        countryDataList.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        data.setCountries(countryDataList);
        log.info("TechMap 数据: {} 个国家, categoryCode={}", countryDataList.size(), categoryCode);
        return data;
    }

    /**
     * 按国家和关键词统计专利数量
     */
    private long countPatentsByCountryAndKeywords(String country, List<String> keywords) {
        LambdaQueryWrapper<Patent> wrapper = new LambdaQueryWrapper<Patent>().eq(Patent::getCountry, country);
        if (!keywords.isEmpty()) {
            wrapper.and(w -> {
                for (String keyword : keywords) {
                    w.or().like(Patent::getTitle, keyword)
                     .or().like(Patent::getTitleKeywords, keyword);
                }
            });
        }
        return patentMapper.selectCount(wrapper);
    }

    /**
     * 按国家和关键词统计论文数量
     */
    private long countPapersByCountryAndKeywords(String country, List<String> keywords) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<Paper>().eq(Paper::getCountry, country);
        if (!keywords.isEmpty()) {
            wrapper.and(w -> {
                for (String keyword : keywords) {
                    w.or().like(Paper::getTitle, keyword)
                     .or().like(Paper::getTitleKeywords, keyword);
                }
            });
        }
        return paperMapper.selectCount(wrapper);
    }
    
    private List<CountryData> getFundingOrgData(List<String> keywords) {
        List<CountryData> result = new ArrayList<>();
        
        LambdaQueryWrapper<Project> query = new LambdaQueryWrapper<Project>()
            .isNotNull(Project::getFundingOrg)
            .ne(Project::getFundingOrg, "");
        if (!keywords.isEmpty()) {
            query.and(w -> {
                for (String keyword : keywords) {
                    w.or().like(Project::getProjectName, keyword)
                     .or().like(Project::getTitleKeywords, keyword);
                }
            });
        }
        
        List<Project> projects = projectMapper.selectList(query.select(Project::getFundingOrg).groupBy(Project::getFundingOrg));
        
        for (Project p : projects) {
            if (p.getFundingOrg() != null && !p.getFundingOrg().trim().isEmpty()) {
                String fundingOrg = p.getFundingOrg().trim();
                CountryData cd = new CountryData();
                cd.setCountry(fundingOrg);
                
                LambdaQueryWrapper<Project> countQuery = new LambdaQueryWrapper<Project>().eq(Project::getFundingOrg, fundingOrg);
                if (!keywords.isEmpty()) {
                    countQuery.and(w -> {
                        for (String keyword : keywords) {
                            w.or().like(Project::getProjectName, keyword)
                             .or().like(Project::getTitleKeywords, keyword);
                        }
                    });
                }
                cd.setCount(projectMapper.selectCount(countQuery));
                if (cd.getCount() > 0) {
                    result.add(cd);
                }
            }
        }
        
        result.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        if (result.size() > 20) {
            result = result.subList(0, 20);
        }
        
        return result;
    }

    public TrendData getTrendData(String country, Integer startYear, Integer endYear, String categoryCode) {
        log.info("查询 Trend 数据, country={}, startYear={}, endYear={}, categoryCode={}", country, startYear, endYear, categoryCode);
        return queryTrendFromDB(country, startYear, endYear, categoryCode);
    }
    
    private TrendData queryTrendFromDB(String country, Integer startYear, Integer endYear, String categoryCode) {
        TrendData data = new TrendData();
        List<YearlyData> yearlyDataList = new ArrayList<>();
        List<String> keywords = getCategoryKeywords(categoryCode);
        
        int start = startYear != null ? startYear : getMinYear();
        int end = endYear != null ? endYear : getMaxYear();
        
        if (start > end || start < 1900 || end > 2100) {
            start = 2020;
            end = 2024;
        }
        
        log.info("趋势分析年份范围: {} - {}, 国家: {}, 分类: {}", start, end, country, categoryCode);
        
        for (int year = start; year <= end; year++) {
            YearlyData yd = new YearlyData();
            yd.setYear(year);
            
            LambdaQueryWrapper<Patent> patentWrapper = new LambdaQueryWrapper<>();
            patentWrapper.eq(Patent::getPublicationYear, year);
            if (country != null && !country.isEmpty()) {
                patentWrapper.eq(Patent::getCountry, country);
            }
            if (!keywords.isEmpty()) {
                patentWrapper.and(w -> {
                    for (String keyword : keywords) {
                        w.or().like(Patent::getTitle, keyword)
                         .or().like(Patent::getTitleKeywords, keyword);
                    }
                });
            }
            yd.setPatentCount(patentMapper.selectCount(patentWrapper));
            
            LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
            projectWrapper.eq(Project::getProjectYear, year);
            yd.setProjectCount(projectMapper.selectCount(projectWrapper));
            
            LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
            paperWrapper.eq(Paper::getPublicationYear, year);
            if (country != null && !country.isEmpty()) {
                paperWrapper.eq(Paper::getCountry, country);
            }
            yd.setPaperCount(paperMapper.selectCount(paperWrapper));
            yearlyDataList.add(yd);
        }
        data.setYearlyData(yearlyDataList);
        return data;
    }
    
    private int getMinYear() {
        int minYear = 2020;
        try {
            List<Patent> patents = patentMapper.selectList(
                new QueryWrapper<Patent>().select("MIN(publication_year) as publication_year").isNotNull("publication_year"));
            if (!patents.isEmpty() && patents.get(0).getPublicationYear() != null) {
                minYear = Math.min(minYear, patents.get(0).getPublicationYear());
            }
            List<Project> projects = projectMapper.selectList(
                new QueryWrapper<Project>().select("MIN(project_year) as project_year").isNotNull("project_year"));
            if (!projects.isEmpty() && projects.get(0).getProjectYear() != null) {
                minYear = Math.min(minYear, projects.get(0).getProjectYear());
            }
            List<Paper> papers = paperMapper.selectList(
                new QueryWrapper<Paper>().select("MIN(publication_year) as publication_year").isNotNull("publication_year"));
            if (!papers.isEmpty() && papers.get(0).getPublicationYear() != null) {
                minYear = Math.min(minYear, papers.get(0).getPublicationYear());
            }
        } catch (Exception e) {
            log.warn("获取最小年份失败", e);
        }
        return minYear;
    }
    
    private int getMaxYear() {
        int maxYear = LocalDate.now().getYear();
        try {
            List<Patent> patents = patentMapper.selectList(
                new QueryWrapper<Patent>().select("MAX(publication_year) as publication_year").isNotNull("publication_year"));
            if (!patents.isEmpty() && patents.get(0).getPublicationYear() != null) {
                maxYear = Math.max(maxYear, patents.get(0).getPublicationYear());
            }
            List<Project> projects = projectMapper.selectList(
                new QueryWrapper<Project>().select("MAX(project_year) as project_year").isNotNull("project_year"));
            if (!projects.isEmpty() && projects.get(0).getProjectYear() != null) {
                maxYear = Math.max(maxYear, projects.get(0).getProjectYear());
            }
            List<Paper> papers = paperMapper.selectList(
                new QueryWrapper<Paper>().select("MAX(publication_year) as publication_year").isNotNull("publication_year"));
            if (!papers.isEmpty() && papers.get(0).getPublicationYear() != null) {
                maxYear = Math.max(maxYear, papers.get(0).getPublicationYear());
            }
        } catch (Exception e) {
            log.warn("获取最大年份失败", e);
        }
        return maxYear;
    }

    public CompetitionData getCompetitionData(String categoryCode) {
        log.info("查询 Competition 数据, categoryCode={}", categoryCode);
        return queryCompetitionFromDB(categoryCode);
    }
    
    private CompetitionData queryCompetitionFromDB(String categoryCode) {
        CompetitionData data = new CompetitionData();
        List<CountryCompetition> competitions = new ArrayList<>();
        List<String> keywords = getCategoryKeywords(categoryCode);
        
        Set<String> countries = getAllCountries();
        if (countries.isEmpty()) {
            countries = new LinkedHashSet<>(Arrays.asList("中国", "美国", "日本", "德国", "韩国"));
        }
        
        long totalProjectCount = projectMapper.selectCount(null);
        
        for (String country : countries) {
            CountryCompetition cc = new CountryCompetition();
            cc.setCountry(country);
            cc.setPatentCount(countPatentsByCountryAndKeywords(country, keywords));
            cc.setProjectCount(0L);
            cc.setPaperCount(paperMapper.selectCount(
                new LambdaQueryWrapper<Paper>().eq(Paper::getCountry, country)));
            
            if (cc.getPatentCount() > 0 || cc.getPaperCount() > 0) {
                competitions.add(cc);
            }
        }
        
        competitions.sort((a, b) -> Long.compare(
            b.getPatentCount() + b.getProjectCount() + b.getPaperCount(),
            a.getPatentCount() + a.getProjectCount() + a.getPaperCount()));
        
        if (competitions.size() > 10) {
            competitions = competitions.subList(0, 10);
        }
        
        data.setCompetitions(competitions);
        data.setTotalProjectCount(totalProjectCount);
        return data;
    }

    public ComparisonData getComparisonData(String countryA, String countryB) {
        ComparisonData data = new ComparisonData();
        data.setCountryA(getCountryMetrics(countryA));
        data.setCountryB(getCountryMetrics(countryB));
        return data;
    }

    private CountryMetrics getCountryMetrics(String country) {
        CountryMetrics metrics = new CountryMetrics();
        metrics.setCountry(country);
        metrics.setPatentCount(patentMapper.selectCount(
            new LambdaQueryWrapper<Patent>().eq(Patent::getCountry, country)));
        metrics.setProjectCount(projectMapper.selectCount(
            new LambdaQueryWrapper<Project>().eq(Project::getCountry, country)));
        metrics.setPaperCount(paperMapper.selectCount(
            new LambdaQueryWrapper<Paper>().eq(Paper::getCountry, country)));
        return metrics;
    }

    public NetworkData getNetworkData(String country, String categoryCode) {
        log.info("查询 Network 数据, country={}, categoryCode={}", country, categoryCode);
        return queryNetworkFromDB(country, categoryCode);
    }
    
    private NetworkData queryNetworkFromDB(String country, String categoryCode) {
        NetworkData data = new NetworkData();
        List<NetworkNode> nodes = new ArrayList<>();
        List<NetworkLink> links = new ArrayList<>();
        List<String> categoryKeywords = getCategoryKeywords(categoryCode);
        
        // 技术关键词统计和共现关系
        Map<String, Integer> keywordCount = new HashMap<>();
        Map<String, Map<String, Integer>> coOccurrence = new HashMap<>();
        
        // 从专利中提取关键词共现关系
        LambdaQueryWrapper<Patent> patentQuery = new LambdaQueryWrapper<Patent>()
            .isNotNull(Patent::getTitleKeywords).ne(Patent::getTitleKeywords, "");
        if (country != null && !country.isEmpty()) {
            patentQuery.eq(Patent::getCountry, country);
        }
        if (!categoryKeywords.isEmpty()) {
            patentQuery.and(w -> {
                for (String keyword : categoryKeywords) {
                    w.or().like(Patent::getTitle, keyword)
                     .or().like(Patent::getTitleKeywords, keyword);
                }
            });
        }
        List<Patent> patents = patentMapper.selectList(patentQuery);
        
        for (Patent p : patents) {
            String keywords = p.getTitleKeywords();
            if (keywords != null && !keywords.isEmpty()) {
                List<String> kwList = new ArrayList<>();
                for (String kw : keywords.split("[,;，；\\s]+")) {
                    String k = kw.trim();
                    if (k.length() >= 2 && k.length() <= 15) {
                        kwList.add(k);
                        keywordCount.merge(k, 1, Integer::sum);
                    }
                }
                // 计算关键词共现（同一专利中出现的关键词对）
                for (int i = 0; i < kwList.size(); i++) {
                    for (int j = i + 1; j < kwList.size(); j++) {
                        String kw1 = kwList.get(i);
                        String kw2 = kwList.get(j);
                        // 确保顺序一致
                        if (kw1.compareTo(kw2) > 0) {
                            String temp = kw1;
                            kw1 = kw2;
                            kw2 = temp;
                        }
                        coOccurrence.computeIfAbsent(kw1, k -> new HashMap<>())
                            .merge(kw2, 1, Integer::sum);
                    }
                }
            }
        }
        
        // 从项目中提取关键词共现关系
        QueryWrapper<Project> projectQuery = new QueryWrapper<Project>()
            .isNotNull("title_keywords").ne("title_keywords", "");
        if (country != null && !country.isEmpty()) {
            projectQuery.eq("country", country);
        }
        List<Project> projects = projectMapper.selectList(projectQuery);
        
        for (Project p : projects) {
            String keywords = p.getTitleKeywords();
            if (keywords != null && !keywords.isEmpty()) {
                List<String> kwList = new ArrayList<>();
                for (String kw : keywords.split("[,;，；\\s]+")) {
                    String k = kw.trim();
                    if (k.length() >= 2 && k.length() <= 15) {
                        kwList.add(k);
                        keywordCount.merge(k, 1, Integer::sum);
                    }
                }
                for (int i = 0; i < kwList.size(); i++) {
                    for (int j = i + 1; j < kwList.size(); j++) {
                        String kw1 = kwList.get(i);
                        String kw2 = kwList.get(j);
                        if (kw1.compareTo(kw2) > 0) {
                            String temp = kw1;
                            kw1 = kw2;
                            kw2 = temp;
                        }
                        coOccurrence.computeIfAbsent(kw1, k -> new HashMap<>())
                            .merge(kw2, 1, Integer::sum);
                    }
                }
            }
        }
        
        // 取TOP关键词作为节点
        List<Map.Entry<String, Integer>> topKeywords = keywordCount.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(30)
            .collect(Collectors.toList());
        
        Set<String> topKeywordSet = topKeywords.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        Map<String, String> nodeIdMap = new HashMap<>();
        int nodeId = 1;
        
        // 动态计算分类阈值（基于数据分布）
        // 前1/3为core，中间1/3为related，后1/3为peripheral
        int totalNodes = topKeywords.size();
        int coreThreshold = totalNodes > 0 ? totalNodes / 3 : 0;
        int relatedThreshold = totalNodes > 0 ? totalNodes * 2 / 3 : 0;
        
        // 创建节点
        int index = 0;
        for (Map.Entry<String, Integer> entry : topKeywords) {
            String id = "t" + nodeId++;
            nodeIdMap.put(entry.getKey(), id);
            // 根据排名分类：前1/3=core，中间1/3=related，后1/3=peripheral
            String category;
            if (index < coreThreshold) {
                category = "core";
            } else if (index < relatedThreshold) {
                category = "related";
            } else {
                category = "peripheral";
            }
            nodes.add(new NetworkNode(id, entry.getKey(), category, entry.getValue()));
            index++;
        }
        
        // 创建连线（只保留TOP关键词之间的关系）
        Set<String> addedLinks = new HashSet<>();
        for (Map.Entry<String, Map<String, Integer>> entry : coOccurrence.entrySet()) {
            String kw1 = entry.getKey();
            if (!topKeywordSet.contains(kw1)) continue;
            
            String sourceId = nodeIdMap.get(kw1);
            if (sourceId == null) continue;
            
            // 取每个关键词的TOP5关联关键词
            entry.getValue().entrySet().stream()
                .filter(e -> topKeywordSet.contains(e.getKey()))
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> {
                    String targetId = nodeIdMap.get(e.getKey());
                    if (targetId != null) {
                        String linkKey = sourceId.compareTo(targetId) < 0 
                            ? sourceId + "-" + targetId 
                            : targetId + "-" + sourceId;
                        if (!addedLinks.contains(linkKey) && e.getValue() >= 2) {
                            links.add(new NetworkLink(sourceId, targetId, e.getValue()));
                            addedLinks.add(linkKey);
                        }
                    }
                });
        }
        
        if (nodes.isEmpty()) {
            nodes.add(new NetworkNode("1", "暂无数据", "info", 1));
        }
        
        data.setNodes(nodes);
        data.setLinks(links);
        log.info("技术关系网络: {} 个节点, {} 条连线", nodes.size(), links.size());
        return data;
    }
    
    /**
     * 清除所有分析相关的缓存（数据导入后调用）
     */
    @CacheEvict(cacheNames = {"techmap", "trend", "category", "stats", "network", "hottech", "maturity", "region"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除所有分析缓存");
    }
    
    /**
     * 热门技术排行 - 基于关键词统计
     */
    @Cacheable(cacheNames = "hottech", key = "#categoryCode ?: 'all'")
    public HotTechData getHotTechData(String categoryCode) {
        log.info("HotTech 缓存未命中，从数据库查询, categoryCode={}", categoryCode);
        return queryHotTechFromDB(categoryCode);
    }
    
    private HotTechData queryHotTechFromDB(String categoryCode) {
        HotTechData data = new HotTechData();
        Map<String, Long> keywordCount = new HashMap<>();
        Map<String, Long> keywordCountLastYear = new HashMap<>();
        List<String> categoryKeywords = getCategoryKeywords(categoryCode);
        
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;
        
        // 统计专利关键词
        LambdaQueryWrapper<Patent> patentQuery = new LambdaQueryWrapper<Patent>()
            .isNotNull(Patent::getTitleKeywords).ne(Patent::getTitleKeywords, "");
        if (!categoryKeywords.isEmpty()) {
            patentQuery.and(w -> {
                for (String keyword : categoryKeywords) {
                    w.or().like(Patent::getTitle, keyword)
                     .or().like(Patent::getTitleKeywords, keyword);
                }
            });
        }
        List<Patent> patents = patentMapper.selectList(patentQuery);
        for (Patent p : patents) {
            String keywords = p.getTitleKeywords();
            if (keywords != null && !keywords.isEmpty()) {
                for (String kw : keywords.split("[,;，；\\s]+")) {
                    String k = kw.trim();
                    if (k.length() >= 2 && k.length() <= 20) {
                        keywordCount.merge(k, 1L, Long::sum);
                        if (p.getPublicationYear() != null && p.getPublicationYear() == lastYear) {
                            keywordCountLastYear.merge(k, 1L, Long::sum);
                        }
                    }
                }
            }
        }
        
        // 统计项目关键词
        List<Project> projects = projectMapper.selectList(
            new QueryWrapper<Project>().isNotNull("title_keywords").ne("title_keywords", ""));
        for (Project p : projects) {
            String keywords = p.getTitleKeywords();
            if (keywords != null && !keywords.isEmpty()) {
                for (String kw : keywords.split("[,;，；\\s]+")) {
                    String k = kw.trim();
                    if (k.length() >= 2 && k.length() <= 20) {
                        keywordCount.merge(k, 1L, Long::sum);
                        if (p.getProjectYear() != null && p.getProjectYear() == lastYear) {
                            keywordCountLastYear.merge(k, 1L, Long::sum);
                        }
                    }
                }
            }
        }
        
        // 取TOP10并计算增长率
        List<HotTechData.TechRank> rankings = keywordCount.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .map(e -> {
                long current = e.getValue();
                long last = keywordCountLastYear.getOrDefault(e.getKey(), 0L);
                double growth = last > 0 ? ((double)(current - last) / last * 100) : 0;
                return new HotTechData.TechRank(e.getKey(), current, Math.round(growth * 10) / 10.0);
            })
            .collect(Collectors.toList());
        
        data.setRankings(rankings);
        return data;
    }
    
    /**
     * 技术成熟度 - 基于年份分布
     */
    @Cacheable(cacheNames = "maturity", key = "#categoryCode ?: 'all'")
    public TechMaturityData getTechMaturityData(String categoryCode) {
        log.info("Maturity 缓存未命中，从数据库查询, categoryCode={}", categoryCode);
        return queryMaturityFromDB(categoryCode);
    }
    
    private TechMaturityData queryMaturityFromDB(String categoryCode) {
        TechMaturityData data = new TechMaturityData();
        int currentYear = LocalDate.now().getYear();
        List<String> categoryKeywords = getCategoryKeywords(categoryCode);
        
        // 新兴技术：近2年
        LambdaQueryWrapper<Patent> emergingPatentQuery = new LambdaQueryWrapper<Patent>()
            .ge(Patent::getPublicationYear, currentYear - 1);
        if (!categoryKeywords.isEmpty()) {
            emergingPatentQuery.and(w -> {
                for (String keyword : categoryKeywords) {
                    w.or().like(Patent::getTitle, keyword)
                     .or().like(Patent::getTitleKeywords, keyword);
                }
            });
        }
        long emergingPatent = patentMapper.selectCount(emergingPatentQuery);
        
        LambdaQueryWrapper<Project> emergingProjectQuery = new LambdaQueryWrapper<Project>()
            .ge(Project::getProjectYear, currentYear - 1);
        if (!categoryKeywords.isEmpty()) {
            emergingProjectQuery.and(w -> {
                for (String keyword : categoryKeywords) {
                    w.or().like(Project::getProjectName, keyword)
                     .or().like(Project::getTitleKeywords, keyword);
                }
            });
        }
        long emergingProject = projectMapper.selectCount(emergingProjectQuery);
        
        // 成长技术：3-5年
        long growingPatent = patentMapper.selectCount(
            new LambdaQueryWrapper<Patent>()
                .ge(Patent::getPublicationYear, currentYear - 5)
                .lt(Patent::getPublicationYear, currentYear - 1));
        long growingProject = projectMapper.selectCount(
            new LambdaQueryWrapper<Project>()
                .ge(Project::getProjectYear, currentYear - 5)
                .lt(Project::getProjectYear, currentYear - 1));
        
        // 成熟技术：5年以上
        long maturePatent = patentMapper.selectCount(
            new LambdaQueryWrapper<Patent>().lt(Patent::getPublicationYear, currentYear - 5));
        long matureProject = projectMapper.selectCount(
            new LambdaQueryWrapper<Project>().lt(Project::getProjectYear, currentYear - 5));
        
        long emerging = emergingPatent + emergingProject;
        long growing = growingPatent + growingProject;
        long mature = maturePatent + matureProject;
        long total = emerging + growing + mature;
        
        data.setEmergingCount(emerging);
        data.setGrowingCount(growing);
        data.setMatureCount(mature);
        
        if (total > 0) {
            data.setEmergingPercent(Math.round(emerging * 1000.0 / total) / 10.0);
            data.setGrowingPercent(Math.round(growing * 1000.0 / total) / 10.0);
            data.setMaturePercent(Math.round(mature * 1000.0 / total) / 10.0);
        }
        
        return data;
    }
    
    /**
     * 区域分布 - 基于国家/地区统计
     */
    @Cacheable(cacheNames = "region", key = "#categoryCode ?: 'all'")
    public RegionDistributionData getRegionDistributionData(String categoryCode) {
        log.info("Region 缓存未命中，从数据库查询, categoryCode={}", categoryCode);
        return queryRegionFromDB(categoryCode);
    }
    
    private RegionDistributionData queryRegionFromDB(String categoryCode) {
        RegionDistributionData data = new RegionDistributionData();
        List<String> categoryKeywords = getCategoryKeywords(categoryCode);
        
        // 定义区域映射
        Map<String, List<String>> regionMapping = new HashMap<>();
        regionMapping.put("北美", Arrays.asList("美国", "USA", "US", "加拿大", "Canada"));
        regionMapping.put("亚洲", Arrays.asList("中国", "China", "CN", "日本", "Japan", "JP", "韩国", "Korea", "KR", "印度", "India"));
        regionMapping.put("欧洲", Arrays.asList("德国", "Germany", "DE", "英国", "UK", "GB", "法国", "France", "FR", "意大利", "Italy"));
        
        Map<String, Long> regionCount = new HashMap<>();
        regionCount.put("北美", 0L);
        regionCount.put("亚洲", 0L);
        regionCount.put("欧洲", 0L);
        regionCount.put("其他", 0L);
        
        // 统计专利
        List<Patent> patents = patentMapper.selectList(
            new QueryWrapper<Patent>().isNotNull("country").ne("country", ""));
        for (Patent p : patents) {
            String country = p.getCountry();
            String region = getRegion(country, regionMapping);
            regionCount.merge(region, 1L, Long::sum);
        }
        
        // 统计论文
        List<Paper> papers = paperMapper.selectList(
            new QueryWrapper<Paper>().isNotNull("country").ne("country", ""));
        for (Paper p : papers) {
            String country = p.getCountry();
            String region = getRegion(country, regionMapping);
            regionCount.merge(region, 1L, Long::sum);
        }
        
        long total = regionCount.values().stream().mapToLong(Long::longValue).sum();
        
        List<RegionDistributionData.RegionData> regions = regionCount.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .map(e -> {
                double percent = total > 0 ? Math.round(e.getValue() * 1000.0 / total) / 10.0 : 0;
                return new RegionDistributionData.RegionData(e.getKey(), e.getValue(), percent);
            })
            .collect(Collectors.toList());
        
        data.setRegions(regions);
        return data;
    }
    
    private String getRegion(String country, Map<String, List<String>> regionMapping) {
        if (country == null) return "其他";
        for (Map.Entry<String, List<String>> entry : regionMapping.entrySet()) {
            for (String c : entry.getValue()) {
                if (country.contains(c) || c.contains(country)) {
                    return entry.getKey();
                }
            }
        }
        return "其他";
    }
}
