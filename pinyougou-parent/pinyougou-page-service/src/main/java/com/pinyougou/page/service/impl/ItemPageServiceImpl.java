package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemPageServiceImpl implements ItemPageService {
	@Value("${pagedir}")
	private String pagedir;
@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	@Autowired
	private TbItemMapper tbItemMapper;
	@Override
	public boolean genItemHtml(Long goodsId) {
		// TODO Auto-generated method stub
		
		try {
			Configuration configuration = freeMarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			//创建数据模型
			Map dataModel = new HashMap<>();
			//1.加载商品表数据
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", goods);
			//2.加载商品扩展表数据
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			//3.商品分类
			String itemCat1 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			String itemCat2 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			String itemCat3 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			dataModel.put("itemCat1",itemCat1);
			dataModel.put("itemCat2",itemCat2);
			dataModel.put("itemCat3",itemCat3);
			Writer out = new FileWriter(pagedir+goodsId+".html");
			//4.SKU列表
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");//状态有效
			criteria.andGoodsIdEqualTo(goodsId);//指定spu ID
			example.setOrderByClause("is_default desc");//按照状态降序
			List<TbItem> itemList = tbItemMapper.selectByExample(example);
			dataModel.put("itemList",itemList);

			//输出
			template.process(dataModel, out);
			out.close();
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	
	}

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
