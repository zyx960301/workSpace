package com.domain;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.domain.TbContent;
import com.domain.TbContentMapper;
import com.domain.TbContentService;

@Service
public class TbContentServiceImpl implements TbContentService{

    @Resource
    private TbContentMapper tbContentMapper;

    @Override
    public int insert(TbContent tbContent){
        return tbContentMapper.insert(tbContent);
    }

    @Override
    public int insertSelective(TbContent tbContent){
        return tbContentMapper.insertSelective(tbContent);
    }

    @Override
    public int insertList(List<TbContent> tbContents){
        return tbContentMapper.insertList(tbContents);
    }

    @Override
    public int updateByPrimaryKeySelective(TbContent tbContent){
        return tbContentMapper.updateByPrimaryKeySelective(tbContent);
    }
}
