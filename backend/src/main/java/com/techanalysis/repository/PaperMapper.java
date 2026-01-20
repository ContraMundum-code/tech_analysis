package com.techanalysis.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techanalysis.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
}
