package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseBaseRepository extends JpaRepository<CourseBase,String> {
}
