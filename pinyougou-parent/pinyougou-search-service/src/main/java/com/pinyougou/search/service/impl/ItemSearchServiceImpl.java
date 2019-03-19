package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public Map search(Map searchMap) {
		Map<String,Object> map=new HashMap<>();
		//关键词空格处理
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords",keywords.replace(" ",""));
		//查询列表高亮显示
		map.putAll(searchList(searchMap));
		//根据关键字查询商品分类
		List categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		String categoryName = (String) searchMap.get("category");
		if(!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		}
		else {
			if(categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
			}
		}
	
		return map;
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品ID"+goodsIdList);
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid");
		criteria.in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

	//关键词搜索列表
	private Map searchList(Map searchMap) {
		Map map=new HashMap();
		//高亮选项初始化
		HighlightQuery query=new SimpleHighlightQuery();		
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
		highlightOptions.setSimplePostfix("</em>");		
		query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项
		//1.1 关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		
		//1.2按分类筛选
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.3 按品牌筛选
		if(!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.4 按规格筛选
		if(searchMap.get("spec")!=null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for(String key:specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.5 按价格筛选
		if(!"".equals(searchMap.get("price"))){
			String [] price= ((String) searchMap.get("price")).split("-");
			if(!price[0].equals("0")){//如果区间起点不等于0
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if(!price[1].equals("*")){//如果区间末尾不等于无穷
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//1.6 分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
		if(pageNo == null){
			pageNo = 1;//默认当前页为1
		}
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if(pageSize == null){
			pageSize  = 20;//默认每页显示20条记录
		}
		query.setOffset((pageNo-1)*pageSize);//起始索引
		query.setRows(pageSize);//每页记录数
		//1.7排序

		String sortValue = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if(sortValue!=null && !sortValue.equals("")){
			if(sortValue.equals("ASC")){
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
			}
			if(sortValue.equals("DESC")){
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
			}
		}

		//***********  获取高亮结果集  ***********
		//高亮页对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		//高亮入口集合(每条记录的高亮入口)
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();		
		for(HighlightEntry<TbItem> entry:entryList  ){
			//获取高亮列表(高亮域的个数)
			List<Highlight> highlightList = entry.getHighlights();	
			if(highlightList.size()>0 &&  highlightList.get(0).getSnipplets().size()>0 ){
				TbItem item = entry.getEntity();
				item.setTitle(highlightList.get(0).getSnipplets().get(0));			
			}			
		}
		map.put("rows", page.getContent());
		map.put("totalPages",page.getTotalPages());//返回总页数
		map.put("total",page.getTotalElements());//返回总记录数
		return map;
	}
	//查询商品分类列表
	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList();
		Query query = new SimpleQuery();
		//按照关键字查询 相当于where
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组选项 相当于 groupBy
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for(GroupEntry<TbItem> entry : content) {
			list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
		}
		return list;
	}
	//查询品牌和规格列表
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		//根据商品分类获得模板Id
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId!=null) {
			//根据模板Id获取商品列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			//根据模板Id获取规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}

}
