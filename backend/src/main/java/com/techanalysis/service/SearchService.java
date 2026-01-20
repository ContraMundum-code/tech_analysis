package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techanalysis.dto.SearchResult;
import com.techanalysis.dto.SearchResult.SearchItem;
import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import com.techanalysis.repository.PaperMapper;
import com.techanalysis.repository.PatentMapper;
import com.techanalysis.repository.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    private static final int MAX_RESULTS = 10;

    private final PatentMapper patentMapper;
    private final PaperMapper paperMapper;
    private final ProjectMapper projectMapper;

    public SearchService(PatentMapper patentMapper, PaperMapper paperMapper, ProjectMapper projectMapper) {
        this.patentMapper = patentMapper;
        this.paperMapper = paperMapper;
        this.projectMapper = projectMapper;
    }

    public SearchResult search(String keyword) {
        SearchResult result = new SearchResult();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            result.setPatents(new ArrayList<>());
            result.setPapers(new ArrayList<>());
            result.setProjects(new ArrayList<>());
            result.setTotalCount(0);
            return result;
        }

        String kw = keyword.trim();
        log.info("全局搜索: {}", kw);

        // 搜索专利
        List<SearchItem> patents = searchPatents(kw);
        result.setPatents(patents);

        // 搜索论文
        List<SearchItem> papers = searchPapers(kw);
        result.setPapers(papers);

        // 搜索项目
        List<SearchItem> projects = searchProjects(kw);
        result.setProjects(projects);

        result.setTotalCount(patents.size() + papers.size() + projects.size());
        log.info("搜索结果: 专利={}, 论文={}, 项目={}", patents.size(), papers.size(), projects.size());

        return result;
    }

    private List<SearchItem> searchPatents(String keyword) {
        List<SearchItem> items = new ArrayList<>();
        try {
            LambdaQueryWrapper<Patent> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w
                .like(Patent::getTitle, keyword)
                .or().like(Patent::getTitleKeywords, keyword)
                .or().like(Patent::getCountry, keyword)
            );
            wrapper.last("LIMIT " + MAX_RESULTS);

            List<Patent> patents = patentMapper.selectList(wrapper);
            for (Patent p : patents) {
                items.add(new SearchItem(
                    p.getId(),
                    p.getTitle(),
                    "patent",
                    p.getCountry(),
                    p.getPublicationYear(),
                    p.getTitleKeywords()
                ));
            }
        } catch (Exception e) {
            log.error("搜索专利失败", e);
        }
        return items;
    }

    private List<SearchItem> searchPapers(String keyword) {
        List<SearchItem> items = new ArrayList<>();
        try {
            LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w
                .like(Paper::getTitle, keyword)
                .or().like(Paper::getTitleKeywords, keyword)
                .or().like(Paper::getCountry, keyword)
            );
            wrapper.last("LIMIT " + MAX_RESULTS);

            List<Paper> papers = paperMapper.selectList(wrapper);
            for (Paper p : papers) {
                items.add(new SearchItem(
                    p.getId(),
                    p.getTitle(),
                    "paper",
                    p.getCountry(),
                    p.getPublicationYear(),
                    p.getTitleKeywords()
                ));
            }
        } catch (Exception e) {
            log.error("搜索论文失败", e);
        }
        return items;
    }

    private List<SearchItem> searchProjects(String keyword) {
        List<SearchItem> items = new ArrayList<>();
        try {
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w
                .like(Project::getProjectName, keyword)
                .or().like(Project::getTitleKeywords, keyword)
                .or().like(Project::getFundingOrg, keyword)
            );
            wrapper.last("LIMIT " + MAX_RESULTS);

            List<Project> projects = projectMapper.selectList(wrapper);
            for (Project p : projects) {
                items.add(new SearchItem(
                    p.getId(),
                    p.getProjectName(),
                    "project",
                    p.getFundingOrg(),
                    p.getProjectYear(),
                    p.getTitleKeywords()
                ));
            }
        } catch (Exception e) {
            log.error("搜索项目失败", e);
        }
        return items;
    }
}
