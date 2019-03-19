package com.tensquare.base.web.controller;

import com.tensquare.base.po.Label;
import com.tensquare.base.service.LabelService;
import constants.StatusCode;
import dto.PageResultDTO;
import dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@Controller
//@ResponseBody
@RestController
@RequestMapping("/label")
@CrossOrigin
public class LabelController {
    //注入service
    @Autowired
    private LabelService labelService;

    /**
     * 添加标签
     * @param label
     * @return
     */
    @PostMapping
    public ResultDTO add(@RequestBody Label label){
        //业务层调用
        labelService.saveLabel(label);
        return new ResultDTO(true, StatusCode.OK,"添加成功");
    }

    /**
     * 修改
     * @param label
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public ResultDTO edit(@RequestBody Label label,@PathVariable String id){
        //设置id
        label.setId(id);
        labelService.updateLabel(label);
        return new ResultDTO(true, StatusCode.OK,"修改成功");
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResultDTO remove(@PathVariable String id){
        labelService.deleteLabelById(id);
        return new ResultDTO(true, StatusCode.OK,"删除成功");
    }

    /**
     * 查询所有
      * @return
     */
    @GetMapping
    public ResultDTO list(){
        //制造异常
        int d=1/0;
        List<Label> list = labelService.findLabelList();
        return new ResultDTO(true, StatusCode.OK,"查询成功",list);
    }

    /**
     * 根据id查询一个
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResultDTO listById(@PathVariable String id){
        Label label = labelService.findLabelById(id);
        return new ResultDTO(true, StatusCode.OK,"查询成功",label);
    }

    /**
     * 复杂条件查询
     * @param searchMap
     * @return
     */
    @PostMapping("/search")
    public ResultDTO list(@RequestBody Map<String ,Object> searchMap){
        List<Label> list = labelService.findLabelList(searchMap);
        return new ResultDTO(true, StatusCode.OK,"查询成功",list);
    }

    /**
     * 组合条件分页列表
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/search/{page}/{size}")
    public ResultDTO listPage(@RequestBody Map<String ,Object> searchMap,@PathVariable int page,@PathVariable int size){
        Page<Label> pageResponse = labelService.findLabelListPage(searchMap, page, size);
        PageResultDTO<Label> pageResultDTO=new PageResultDTO<>(
                pageResponse.getTotalElements(),
                pageResponse.getContent()
        );
        return new ResultDTO(true, StatusCode.OK,"查询成功",pageResultDTO);
    }



}
