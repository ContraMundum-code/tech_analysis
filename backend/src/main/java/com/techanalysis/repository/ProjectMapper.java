package com.techanalysis.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techanalysis.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
