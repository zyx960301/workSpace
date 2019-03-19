package com.tensquare.recruit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.recruit.po.Recruit;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface RecruitRepository extends JpaRepository<Recruit,String>,JpaSpecificationExecutor<Recruit>{
    /**
     * 根据状态，查询最新职位列表,按创建日期降序排序，并只查询前4条
     * @param state
     * @return
     */
	List<Recruit> findTop4ByStateOrderByCreatetimeDesc(String state);
}
