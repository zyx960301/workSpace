package com.domain;

import java.util.List;
import com.domain.TbContent;
public interface TbContentService{

    int insert(TbContent tbContent);

    int insertSelective(TbContent tbContent);

    int insertList(List<TbContent> tbContents);

    int updateByPrimaryKeySelective(TbContent tbContent);
}
