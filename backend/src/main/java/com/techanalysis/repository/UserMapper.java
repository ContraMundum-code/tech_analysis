package com.techanalysis.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techanalysis.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
