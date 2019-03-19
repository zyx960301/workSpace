package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.cms.response.CourseBaseResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMarketRepository courseMarketRepository;
    @Autowired
    CoursePicRepository coursePicRepository;
    @Autowired
    CmsPageClient cmsPageClient;
    @Autowired
    CoursePubRepository coursePubRepository;

    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;
    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程id
        String courseid = teachplan.getCourseid();
        //parentId
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            //取出课程父节点
            parentid = this.getTeachplanRoot(courseid);
        }
        //取出父节点信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
//        //父节点
        Teachplan teachplanParent = optional.get();
//        //父节点级别
        String grade = teachplanParent.getGrade();

        //新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面的teachplan信息考到teachNew中
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if (grade.equals("1")) {
            teachplanNew.setGrade("2");
        } else {
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    //获取课程根节点，没有则添加根节点
    private String getTeachplanRoot(String courseId) {
        //查询课程根节点
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            //查询不到，自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        return teachplanList.get(0).getId();
    }

    //查询课程列表
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        // PageHelper分页
        PageHelper.startPage(page, size);
        //获得分页参数
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //得到数据列表
        List<CourseInfo> result = courseListPage.getResult();
        //总页数
        long total = courseListPage.getTotal();
        //结果集对象
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(result);
        queryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    //查询课程分类
    public CategoryNode findCategoryList() {
        return courseMapper.findCategoryList();
    }

    //添加课程
    public CourseBaseResult addCourseBase(CourseBase courseBase) {
        //校验页面是否存在，根据课程名称、课程Id
        CourseBase courseBase1 = courseMapper.findCourseBaseById(courseBase.getId());
        if (courseBase1 != null) {
            //课程已经存在
            //抛出课程已经存在异常
            ExceptionCast.cast(CourseCode.COURSE_ADD_EXISTSNAME);
        }
        courseBase.setId(null);
        courseBaseRepository.save(courseBase);
        //返回结果
        CourseBaseResult courseBaseResult = new CourseBaseResult(CommonCode.SUCCESS, courseBase);
        return courseBaseResult;
    }

    /**
     * 根据id查询课程基本信息
     *
     * @param id
     * @return
     */
    public CourseBase getCourseBaseById(String id) {
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        CourseBase courseBase = optional.get();
        if (courseBase != null) {
            return courseBase;
        }
        return null;
    }

    /**
     * 修改课程基本信息
     *
     * @param id
     * @param courseBase
     * @return
     */
    @Transactional
    public CourseBaseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase courseBase1 = this.getCourseBaseById(id);
        if (courseBase1 != null) {
            //准备更新数据
            //设置要修改的数据
            //更新课程名称
            courseBase1.setName(courseBase.getName());
            //更新适用人群
            courseBase1.setUsers(courseBase.getUsers());
            //更新课程等级
            courseBase1.setGrade(courseBase.getGrade());
            //更新学习模式
            courseBase1.setStudymodel(courseBase.getStudymodel());
            //更新课程所属分类
            courseBase1.setMt(courseBase.getMt());
            courseBase1.setSt(courseBase.getSt());
            //更新课程描述
            courseBase1.setDescription(courseBase.getDescription());
            //提交修改
            CourseBase save = courseBaseRepository.save(courseBase1);
            if (save != null) {
                //返回成功
                return new CourseBaseResult(CommonCode.SUCCESS, save);
            }
        }
        //返回失败
        return new CourseBaseResult(CommonCode.FAIL, null);

    }

    //获取课程营销信息
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        CourseMarket courseMarket = optional.get();
        if (courseMarket != null) {
            return courseMarket;
        }
        return null;
    }

    //更新课程营销信息
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket courseMarket1 = this.getCourseMarketById(id);
        ResponseResult responseResult = new ResponseResult();
        if (courseMarket1 != null) {
            //准备更新数据
            //设置要修改的数据
//            private String charge;
//            private String valid;
//            private String qq;
//            private Float price;
//            private Float price_old;
////    private Date expires;
//            @Column(name = "start_time")
//            private Date startTime;
//            @Column(name = "end_time")
//            private Date endTime;
            //更新课程价格
            courseMarket1.setCharge(courseMarket.getCharge());
            //更新课程金额
            courseMarket1.setPrice(courseMarket.getPrice());
            //更新课程有效期
            courseMarket1.setValid(courseMarket.getValid());
            //更新课程有效开始时间
            courseMarket1.setStartTime(courseMarket.getStartTime());
            //更新课程有效结束时间
            courseMarket1.setEndTime(courseMarket.getEndTime());
            //更新qq
            courseMarket1.setQq(courseMarket.getQq());
            //提交修改
            CourseMarket save = courseMarketRepository.save(courseMarket1);
            if (save != null) {
                //返回成功
                responseResult.setSuccess(true);
                String jsonString = JSON.toJSONString(save);
                responseResult.setMessage(jsonString);
                return responseResult;
            }
        }
        //返回失败
        responseResult.setSuccess(false);
        return responseResult;
    }
    //向课程管理数据添加课程与图片关联信息
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        //查询图片
        CoursePic coursePic =null;
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            coursePic = optional.get();
        }
        if(coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //根据courseId查询图片信息
    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }
    ////根据courseId删除图片信息
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if(result > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //课程视图查询
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程基本信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if(optionalCourseBase.isPresent()){
            CourseBase courseBase = optionalCourseBase.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if(optionalCourseMarket.isPresent()){
            CourseMarket courseMarket = optionalCourseMarket.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程图片信息
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(id);
        if(optionalCoursePic.isPresent()){
            CoursePic coursePic = optionalCoursePic.get();
            courseView.setCoursePic(coursePic);
        }
        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
            courseView.setTeachplanNode(teachplanNode);
            return courseView;
        }
        //页面预览
    public CoursePublishResult preview(String id) {

        //查询课程
        CourseBase courseBase = this.getCourseBaseById(id);
        //1.请求cms添加页面
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //远程调用cms
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        //页面id
        String pageId = cmsPage1.getPageId();
        //2.拼装预览的url
        String pageUrl = previewUrl+pageId;
        //3.返回CoursePublishResult
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBase = this.getCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //一键发布
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程发布状态
        CourseBase courseBaseSave = saveCoursePubState(id);
        if(courseBaseSave == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程索引信息
            //1.创建coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //2.将coursePub保存到数据库中
        CoursePub newCoursePub = saveCoursePub(id, coursePub);
        if(newCoursePub == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //得到页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
    //更新课程发布状态
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBase = this.getCourseBaseById(courseId);
        //更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }
    //创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据id的查询CourseBase存入coursePub
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if(optionalCourseBase.isPresent()){
            CourseBase courseBase = optionalCourseBase.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }
    //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        //将课程计划转成json
        String teachplanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }
    //将coursePub保存到数据库中
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        //根据id查询coursePub信息
        CoursePub coursePubNew = null;
        Optional<CoursePub> optionalCoursePub = coursePubRepository.findById(id);
        if(optionalCoursePub.isPresent()){
            coursePubNew = optionalCoursePub.get();
        }
        else {
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(format);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }
}
