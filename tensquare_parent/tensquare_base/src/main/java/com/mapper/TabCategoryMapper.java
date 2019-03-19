package com.mapper;

import com.domain.TabCategory;

public interface TabCategoryMapper {
    int deleteByPrimaryKey(Integer cid);

    int insert(TabCategory record);

    int insertSelective(TabCategory record);

    TabCategory selectByPrimaryKey(Integer cid);

    int updateByPrimaryKeySelective(TabCategory record);

    int updateByPrimaryKey(TabCategory record);
}