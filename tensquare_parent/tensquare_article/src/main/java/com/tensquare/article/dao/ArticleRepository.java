package com.tensquare.article.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.article.po.Article;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ArticleRepository extends JpaRepository<Article,String>,JpaSpecificationExecutor<Article>{

    /**
     * 修改文章状态
     * @param id
     * @param state
     */
    @Query("update Article set state =?2 where id =?1")
    @Modifying//增删改，要添加该注解，表明是一个可修改的查询，本质是没有事务管理。
    void updateStateById(String id,String state);

    /**
     * 修改点赞的数量，递增或递减
     * @param id
     */
    @Query("update Article set thumbup=thumbup+?2 where id =?1")
    @Modifying
    void updateThumbupToIncrementingOrDiminishing(String id,int thumbup);
}
