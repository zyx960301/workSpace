package com.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.domain.TbContent;

@Mapper
public interface TbContentMapper {
    int insert(@Param("tbContent") TbContent tbContent);

    int insertSelective(@Param("tbContent") TbContent tbContent);

    int insertList(@Param("tbContents") List<TbContent> tbContents);

    int updateByPrimaryKeySelective(@Param("tbContent") TbContent tbContent);
}
