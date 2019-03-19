package com.itheima.ssm.controller;

import com.itheima.ssm.domain.Items;
import com.itheima.ssm.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/items")
public class itemsController {
    @Autowired
    private ItemsService itemService;
    @RequestMapping("/findAll.do")
    public ModelAndView findAll(){
        List<Items>  lists = itemService.findAll();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("itemsList");
        mv.addObject("itemsList",lists);
        return mv;
    }
}
