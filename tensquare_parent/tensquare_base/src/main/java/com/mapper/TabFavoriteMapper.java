package com.mapper;

import com.domain.TabFavorite;
import org.apache.ibatis.annotations.Param;

public interface TabFavoriteMapper {
    int deleteByPrimaryKey(@Param("rid") Integer rid, @Param("uid") Integer uid);

    int insert(TabFavorite record);

    int insertSelective(TabFavorite record);

    TabFavorite selectByPrimaryKey(@Param("rid") Integer rid, @Param("uid") Integer uid);

    int updateByPrimaryKeySelective(TabFavorite record);

    int updateByPrimaryKey(TabFavorite record);
}