package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;


/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {
   //查询课程基本信息
   CourseBase findCourseBaseById(String id);
   //分页查询课程列表
   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
   //课程分类查询
   CategoryNode findCategoryList();
}
