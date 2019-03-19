package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	public void importItemData(){
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//审核为1的才导入
		List<TbItem> itemList = tbItemMapper.selectByExample(example);
		System.out.println("商品列表");
		for(TbItem item : itemList) {
		 Map map = JSON.parseObject(item.getSpec(),Map.class);//从数据库中提取json字符串  放入集合中
		 item.setSpecMap(map);
		}
		//批量导入
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		System.out.println("结束");
	}
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		solrUtil.importItemData();
		solrUtil.testDeleteAll();
	}
	//
	public void testDeleteAll(){
		org.springframework.data.solr.core.query.Query query = new SimpleQuery("*:*");
		solrTemplate.delete(query);
		solrTemplate.commit();
	}	
}

