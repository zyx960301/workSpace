package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据skuid 查询商品信息
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        //2.获取商家id
        String sellerId = item.getSellerId();
        //3.根据商家id判断购物车列表是否含有该商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);

        //4.如果不存在
        if(cart==null){
            //4.1新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //创建购物车明细列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //创建新的购物车明细对象
            TbOrderItem tbOrderItem = createOrderItem(item, num);
            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);

            //4.2将新建的购物车对象添加进购物车列表
        }
        else {
            //5.如果购物车列表存在该商家购物车
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //查询购物车明细列表是否存在该商品
            if(tbOrderItem==null){
                //5.1如果没有，新增明细
               tbOrderItem = createOrderItem(item,num);
               cart.getOrderItemList().add(tbOrderItem);
            }
            else {
                //5.2如果有，增加数量，更改金额
                   tbOrderItem.setNum(tbOrderItem.getNum()+num);
                   tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));
                    //如果数量操作后小于0 移除明细表
                   if(tbOrderItem.getNum()<=0){
                       cart.getOrderItemList().remove(tbOrderItem);
                   }
                   //如果cart 明细数量为0 移除cart
                    if(cart.getOrderItemList().size()<=0){
                       cartList.remove(cart);
                    }

                }
            }

        return cartList;
        }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从reids中查询"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入数据"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                cartList1 = addGoodsToCartList(cartList1, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }
        return cartList1;
    }


    /**
     * 根据商家id查询购物车列表
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     *创建订单明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return tbOrderItem;
    }
    /**
     * 根据商品明细id查询
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem tbOrderItem : orderItemList) {
            if(tbOrderItem.getItemId().longValue()==itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }
}
