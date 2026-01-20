package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.entity.Paper;
import com.techanalysis.repository.PaperMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PaperService {

    private static final Logger log = LoggerFactory.getLogger(PaperService.class);
    private static final int BATCH_SIZE = 1000;

    private final PaperMapper paperMapper;

    public PaperService(PaperMapper paperMapper) {
        this.paperMapper = paperMapper;
    }

    public Page<Paper> list(int page, int size, String country, String keyword) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(country)) {
            wrapper.eq(Paper::getCountry, country);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Paper::getTitle, keyword)
                    .or().like(Paper::getAbstractText, keyword)
                    .or().like(Paper::getAuthors, keyword));
        }
        wrapper.orderByDesc(Paper::getPublicationYear);
        return paperMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Paper getById(Long id) {
        return paperMapper.selectById(id);
    }

    public void save(Paper paper) {
        paperMapper.insert(paper);
    }

    @Transactional
    @CacheEvict(value = "stats", allEntries = true)
    public int saveBatch(List<Paper> papers) {
        int total = papers.size();
        int count = 0;
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Paper> batch = papers.subList(i, end);
            for (Paper paper : batch) {
                paperMapper.insert(paper);
                count++;
            }
            log.info("论文导入进度: {}/{}", count, total);
        }
        return count;
    }

    @Cacheable(value = "stats", key = "'paper:country:' + #country")
    public long countByCountry(String country) {
        return paperMapper.selectCount(
            new LambdaQueryWrapper<Paper>().eq(Paper::getCountry, country)
        );
    }

    @Cacheable(value = "stats", key = "'paper:total'")
    public long count() {
        return paperMapper.selectCount(null);
    }
}
