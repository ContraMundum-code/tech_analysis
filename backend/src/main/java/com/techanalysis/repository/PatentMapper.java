package com.techanalysis.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techanalysis.entity.Patent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatentMapper extends BaseMapper<Patent> {
}
