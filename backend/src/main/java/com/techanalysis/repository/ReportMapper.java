package com.techanalysis.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techanalysis.entity.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {
}
