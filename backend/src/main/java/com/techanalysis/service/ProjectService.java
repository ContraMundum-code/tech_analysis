package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.entity.Project;
import com.techanalysis.repository.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private static final int BATCH_SIZE = 1000;

    private final ProjectMapper projectMapper;

    public ProjectService(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public Page<Project> list(int page, int size, String country, String keyword) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(country)) {
            wrapper.eq(Project::getCountry, country);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Project::getProjectName, keyword)
                    .or().like(Project::getInstitution, keyword));
        }
        wrapper.orderByDesc(Project::getStartDate);
        return projectMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Project getById(Long id) {
        return projectMapper.selectById(id);
    }

    public void save(Project project) {
        projectMapper.insert(project);
    }

    @Transactional
    @CacheEvict(value = "stats", allEntries = true)
    public int saveBatch(List<Project> projects) {
        int total = projects.size();
        int count = 0;
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Project> batch = projects.subList(i, end);
            for (Project project : batch) {
                projectMapper.insert(project);
                count++;
            }
            log.info("项目导入进度: {}/{}", count, total);
        }
        return count;
    }

    @Cacheable(value = "stats", key = "'project:country:' + #country")
    public long countByCountry(String country) {
        return projectMapper.selectCount(
            new LambdaQueryWrapper<Project>().eq(Project::getCountry, country)
        );
    }

    @Cacheable(value = "stats", key = "'project:total'")
    public long count() {
        return projectMapper.selectCount(null);
    }
}
