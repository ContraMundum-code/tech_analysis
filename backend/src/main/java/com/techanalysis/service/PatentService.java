package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.techanalysis.entity.Patent;
import com.techanalysis.repository.PatentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PatentService {

    private static final Logger log = LoggerFactory.getLogger(PatentService.class);
    private static final int BATCH_SIZE = 1000;

    private final PatentMapper patentMapper;

    public PatentService(PatentMapper patentMapper) {
        this.patentMapper = patentMapper;
    }

    public Page<Patent> list(int page, int size, String country, String keyword) {
        LambdaQueryWrapper<Patent> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(country)) {
            wrapper.eq(Patent::getCountry, country);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Patent::getTitle, keyword)
                    .or().like(Patent::getAbstractText, keyword)
                    .or().like(Patent::getApplicant, keyword));
        }
        wrapper.orderByDesc(Patent::getApplicationDate);
        return patentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Patent getById(Long id) {
        return patentMapper.selectById(id);
    }

    public void save(Patent patent) {
        patentMapper.insert(patent);
    }

    @Transactional
    @CacheEvict(value = "stats", allEntries = true)
    public int saveBatch(List<Patent> patents) {
        int total = patents.size();
        int count = 0;
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<Patent> batch = patents.subList(i, end);
            for (Patent patent : batch) {
                patentMapper.insert(patent);
                count++;
            }
            log.info("专利导入进度: {}/{}", count, total);
        }
        return count;
    }

    @Cacheable(value = "stats", key = "'patent:country:' + #country")
    public long countByCountry(String country) {
        return patentMapper.selectCount(
            new LambdaQueryWrapper<Patent>().eq(Patent::getCountry, country)
        );
    }

    @Cacheable(value = "stats", key = "'patent:total'")
    public long count() {
        return patentMapper.selectCount(null);
    }
}
