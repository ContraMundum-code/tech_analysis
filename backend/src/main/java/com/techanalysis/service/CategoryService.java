package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techanalysis.dto.CategoryData;
import com.techanalysis.dto.CategoryData.TechCategory;
import com.techanalysis.dto.CategoryData.TechSubCategory;
import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import com.techanalysis.repository.PaperMapper;
import com.techanalysis.repository.PatentMapper;
import com.techanalysis.repository.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final PatentMapper patentMapper;
    private final PaperMapper paperMapper;
    private final ProjectMapper projectMapper;
    private final ObjectMapper objectMapper;

    // 预定义的技术分类及其关键词映射
    private static final Map<String, CategoryConfig> CATEGORY_CONFIGS = new LinkedHashMap<>();

    static {
        CATEGORY_CONFIGS.put("AI", new CategoryConfig("TP18", "人工智能", "Artificial Intelligence", 
            "机器学习、深度学习、自然语言处理等", "🤖",
            Arrays.asList("人工智能", "机器学习", "深度学习", "神经网络", "自然语言", "计算机视觉", "图像识别", 
                "语音识别", "知识图谱", "智能", "AI", "CNN", "RNN", "LSTM", "Transformer", "GPT", "BERT")));
        
        CATEGORY_CONFIGS.put("CS", new CategoryConfig("TP3", "计算机技术", "Computer Technology",
            "计算机硬件、软件、网络等技术", "💻",
            Arrays.asList("计算机", "软件", "算法", "数据结构", "编程", "程序", "系统", "数据库", 
                "云计算", "大数据", "分布式", "并行计算", "操作系统", "编译")));
        
        CATEGORY_CONFIGS.put("AUTO", new CategoryConfig("TP2", "自动化技术", "Automation Technology",
            "自动控制、机器人技术等", "⚙️",
            Arrays.asList("自动化", "控制", "机器人", "PLC", "传感器", "执行器", "伺服", "运动控制",
                "工业控制", "自动控制", "智能制造", "工业4.0")));
        
        CATEGORY_CONFIGS.put("ELEC", new CategoryConfig("TN", "电子技术", "Electronic Technology",
            "电子电路、半导体技术等", "⚡",
            Arrays.asList("电子", "电路", "半导体", "芯片", "集成电路", "晶体管", "二极管", "电容",
                "电阻", "PCB", "FPGA", "ASIC", "SoC", "微电子")));
        
        CATEGORY_CONFIGS.put("COMM", new CategoryConfig("TN91", "通信技术", "Communication Technology",
            "无线通信、光纤通信等", "📡",
            Arrays.asList("通信", "无线", "5G", "4G", "LTE", "WiFi", "蓝牙", "光纤", "网络",
                "信号", "调制", "编码", "天线", "射频", "微波")));
        
        CATEGORY_CONFIGS.put("CHEM", new CategoryConfig("TQ", "化学工程", "Chemical Engineering",
            "化工工艺、材料化学等", "🧪",
            Arrays.asList("化学", "化工", "催化", "反应", "合成", "聚合", "材料", "高分子",
                "纳米", "涂料", "塑料", "橡胶", "纤维")));
        
        CATEGORY_CONFIGS.put("MECH", new CategoryConfig("TH", "机械工程", "Mechanical Engineering",
            "机械设计、制造工艺等", "🔧",
            Arrays.asList("机械", "制造", "加工", "模具", "铸造", "锻造", "焊接", "切削",
                "数控", "CNC", "3D打印", "增材制造", "齿轮", "轴承")));
        
        CATEGORY_CONFIGS.put("MED", new CategoryConfig("R", "医药卫生", "Medicine & Health",
            "医学研究、药物开发等", "🏥",
            Arrays.asList("医学", "医疗", "药物", "治疗", "诊断", "疾病", "细胞", "基因",
                "蛋白", "抗体", "疫苗", "临床", "手术", "影像")));
        
        CATEGORY_CONFIGS.put("ENERGY", new CategoryConfig("TK", "能源技术", "Energy Technology",
            "新能源、储能技术等", "🔋",
            Arrays.asList("能源", "电池", "锂电", "太阳能", "光伏", "风能", "储能", "氢能",
                "燃料电池", "新能源", "充电", "发电")));
        
        CATEGORY_CONFIGS.put("ENV", new CategoryConfig("X", "环境科学", "Environmental Science",
            "环境保护、污染治理等", "🌿",
            Arrays.asList("环境", "污染", "废水", "废气", "固废", "回收", "净化", "处理",
                "生态", "碳排放", "减排", "绿色")));
    }

    public CategoryService(PatentMapper patentMapper, PaperMapper paperMapper, 
                          ProjectMapper projectMapper, ObjectMapper objectMapper) {
        this.patentMapper = patentMapper;
        this.paperMapper = paperMapper;
        this.projectMapper = projectMapper;
        this.objectMapper = objectMapper;
    }

    @Cacheable(cacheNames = "category", key = "'all'")
    public CategoryData getCategoryData() {
        log.info("Category 缓存未命中，从数据库统计");
        return buildCategoryData();
    }

    private CategoryData buildCategoryData() {
        CategoryData data = new CategoryData();
        List<TechCategory> categories = new ArrayList<>();

        // 获取所有关键词统计
        Map<String, Long> patentKeywords = getKeywordCounts("patent");
        Map<String, Long> paperKeywords = getKeywordCounts("paper");
        Map<String, Long> projectKeywords = getKeywordCounts("project");

        for (Map.Entry<String, CategoryConfig> entry : CATEGORY_CONFIGS.entrySet()) {
            CategoryConfig config = entry.getValue();
            TechCategory category = new TechCategory();
            
            category.setCode(config.code);
            category.setName(config.name);
            category.setEnName(config.enName);
            category.setDescription(config.description);
            category.setIcon(config.icon);

            // 统计该分类下的数量
            long patentCount = countByKeywords(patentKeywords, config.keywords);
            long paperCount = countByKeywords(paperKeywords, config.keywords);
            long projectCount = countByKeywords(projectKeywords, config.keywords);

            category.setPatentCount(patentCount);
            category.setPaperCount(paperCount);
            category.setProjectCount(projectCount);
            category.setTotalCount(patentCount + paperCount + projectCount);

            // 生成子分类（基于热门关键词）
            List<TechSubCategory> subItems = buildSubCategories(config, patentKeywords, paperKeywords, projectKeywords);
            category.setSubItems(subItems);

            categories.add(category);
        }

        // 按总数排序
        categories.sort((a, b) -> Long.compare(b.getTotalCount(), a.getTotalCount()));
        data.setCategories(categories);

        return data;
    }

    private Map<String, Long> getKeywordCounts(String type) {
        Map<String, Long> counts = new HashMap<>();
        
        try {
            List<?> records;
            if ("patent".equals(type)) {
                records = patentMapper.selectList(
                    new QueryWrapper<Patent>().select("title_keywords").isNotNull("title_keywords").ne("title_keywords", ""));
            } else if ("paper".equals(type)) {
                records = paperMapper.selectList(
                    new QueryWrapper<Paper>().select("title_keywords").isNotNull("title_keywords").ne("title_keywords", ""));
            } else {
                records = projectMapper.selectList(
                    new QueryWrapper<Project>().select("title_keywords").isNotNull("title_keywords").ne("title_keywords", ""));
            }

            for (Object record : records) {
                String keywords = null;
                if (record instanceof Patent) keywords = ((Patent) record).getTitleKeywords();
                else if (record instanceof Paper) keywords = ((Paper) record).getTitleKeywords();
                else if (record instanceof Project) keywords = ((Project) record).getTitleKeywords();

                if (keywords != null && !keywords.isEmpty()) {
                    for (String kw : keywords.split("[,;，；\\s]+")) {
                        String k = kw.trim();
                        if (k.length() >= 2 && k.length() <= 20) {
                            counts.merge(k, 1L, Long::sum);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取关键词统计失败: {}", e.getMessage());
        }

        return counts;
    }

    private long countByKeywords(Map<String, Long> keywordCounts, List<String> targetKeywords) {
        long total = 0;
        for (String target : targetKeywords) {
            for (Map.Entry<String, Long> entry : keywordCounts.entrySet()) {
                if (entry.getKey().contains(target) || target.contains(entry.getKey())) {
                    total += entry.getValue();
                }
            }
        }
        return total;
    }

    private List<TechSubCategory> buildSubCategories(CategoryConfig config, 
            Map<String, Long> patentKw, Map<String, Long> paperKw, Map<String, Long> projectKw) {
        
        List<TechSubCategory> subItems = new ArrayList<>();
        
        // 找出该分类下的热门关键词作为子分类
        Map<String, Long> relevantKeywords = new HashMap<>();
        
        for (String target : config.keywords) {
            for (Map.Entry<String, Long> entry : patentKw.entrySet()) {
                if (entry.getKey().contains(target) || target.contains(entry.getKey())) {
                    relevantKeywords.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
            }
            for (Map.Entry<String, Long> entry : paperKw.entrySet()) {
                if (entry.getKey().contains(target) || target.contains(entry.getKey())) {
                    relevantKeywords.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
            }
            for (Map.Entry<String, Long> entry : projectKw.entrySet()) {
                if (entry.getKey().contains(target) || target.contains(entry.getKey())) {
                    relevantKeywords.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
            }
        }

        // 取TOP5作为子分类
        List<Map.Entry<String, Long>> topKeywords = relevantKeywords.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .collect(Collectors.toList());

        int index = 1;
        for (Map.Entry<String, Long> entry : topKeywords) {
            TechSubCategory sub = new TechSubCategory();
            sub.setCode(config.code + "-" + index);
            sub.setName(entry.getKey());
            sub.setEnName(entry.getKey());
            sub.setDescription("相关技术领域");
            sub.setIcon("📌");
            sub.setCount(entry.getValue());
            sub.setKeywords(Arrays.asList(entry.getKey()));
            subItems.add(sub);
            index++;
        }

        return subItems;
    }

    // 配置类
    private static class CategoryConfig {
        String code;
        String name;
        String enName;
        String description;
        String icon;
        List<String> keywords;

        CategoryConfig(String code, String name, String enName, String description, String icon, List<String> keywords) {
            this.code = code;
            this.name = name;
            this.enName = enName;
            this.description = description;
            this.icon = icon;
            this.keywords = keywords;
        }
    }
}
