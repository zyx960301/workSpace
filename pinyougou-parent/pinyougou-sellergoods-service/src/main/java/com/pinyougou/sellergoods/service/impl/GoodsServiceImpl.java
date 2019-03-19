package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper tbBrandMapper;
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	@Autowired
	private TbSellerMapper tbSellerMapper;
	@Autowired
	private TbItemMapper tbItemMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置未申请状态
		goodsMapper.insert(goods.getGoods());//插入商品列表
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置Id
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
        //插入sku列表
        saveItemList(goods);

	}
    private void setItemValus(Goods goods,TbItem tbItem ){
        //存入三级标签
        tbItem.setCategoryid(goods.getGoods().getCategory3Id());
        //创建时间
        tbItem.setCreateTime(new Date());
        //更新时间
        tbItem.setUpdateTime(new Date());
        //存入商品Id
        tbItem.setGoodsId(goods.getGoods().getId());
        //存入商家id
        tbItem.setSellerId(goods.getGoods().getSellerId());
        //获得品牌对象,根据品牌对象得到品牌名称存入tbItem
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //分类名称
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        //商家店铺名称
        TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
        //图片名称
        List<Map> mapList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if(mapList.size()>0){
            tbItem.setImage((String) mapList.get(0).get("url"));
        }
    }
    //插入sku列表
    private void saveItemList(Goods goods){
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            for (TbItem tbItem : goods.getItemList()) {
                //标题 SPU名称+规格属性值
                String title = goods.getGoods().getGoodsName();
                //将字符串转换成JSON串并存入map集合
                Map<String,Object> specMap = JSON.parseObject(tbItem.getSpec());
                //遍历每个key 把value加在title上
                for (String key : specMap.keySet()) {
                    title += " "+specMap.get(key);
                }
                //存入标题
                tbItem.setTitle(title);
                setItemValus(goods,tbItem);
                tbItemMapper.insert(tbItem);
            }
        }
        else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());//存入标题 SPU名称+规格属性值 没有规格属性
            tbItem.setPrice( goods.getGoods().getPrice() );//价格
            tbItem.setStatus("1");//状态
            tbItem.setIsDefault("1");//是否默认
            tbItem.setNum(99999);//库存数量
            tbItem.setSpec("{}");
            setItemValus(goods,tbItem);
            tbItemMapper.insert(tbItem);
        }
    }

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
        TbGoods tbGoods = goods.getGoods();
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        goodsMapper.updateByPrimaryKey(tbGoods);
        goodsDescMapper.updateByPrimaryKey(tbGoodsDesc);
        //删除原有的sku列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        tbItemMapper.deleteByExample(example);
        //添加新的sku数据
        saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
        Goods goods = new Goods();
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        //
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//			goodsMapper.deleteByPrimaryKey(id);
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
			criteria.andIsDeleteIsNull();//非删除
		
		if(goods!=null){			
		    if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));//将spuId数值转换成集合
		criteria.andStatusEqualTo(status);
		List<TbItem> tbItems = tbItemMapper.selectByExample(example);
		return tbItems;
	}
}
