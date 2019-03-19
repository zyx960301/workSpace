package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取登陆用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登陆用户名"+username);
        /**
         * 从cookie查询cartList
         */
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString ="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(username.equals("anonymousUser")){
            //如果未登陆
            System.out.println("从cookie中查询数据");
            return cartList_cookie;
        }
        else {
            //如果登陆 //合并购物车
            //redis中的购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if(cartList_cookie.size()>0){
                //得到合并购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //将合并购物车存入redis
                cartService.saveCartListToRedis(username,cartList);
                //清除本地购物车
                util.CookieUtil.deleteCookie(request,response,"cartList");
                return cartList;
            }

            return cartList_redis;


        }

    }
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//可以访问的域(不操作cookie)
        response.setHeader("Access-Control-Allow-Credentials", "true");//允许使用cookie
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登陆用户名"+username);
        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if(username.equals("anonymousUser")){
                //如果未登陆
               util.CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
                System.out.println("向cookie存入数据");
            }
            else {
                //如果登陆
                cartService.saveCartListToRedis(username,cartList);
                System.out.println("向redis存入数据");
            }

            return new Result(true,"购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"购物车添加失败");
        }
    }
}
