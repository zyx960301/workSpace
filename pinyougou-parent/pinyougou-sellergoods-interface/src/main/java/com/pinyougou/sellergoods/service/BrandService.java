package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
/**
 * 品牌服务层接口
 * @author 57473
 *
 */

import entity.PageResult;
public interface BrandService {
	/**
	 * 查询品牌
	 * @return
	 */
	public List<TbBrand> findAll();
	/**
	 * 品牌分页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	/**
	 * 增加
	 * @param tbBrand
	 */
	public void add(TbBrand tbBrand);
	/**
	 * 根据id查询实体类
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);
	/**
	 * 修改
	 * @param tbBrand
	 */
	public void update(TbBrand tbBrand);
	/**
	 * 删除
	 * @param ids
	 */
	public void delete(Long[] ids);
	/**
	 * 条件查询
	 * @param pageNum
	 * @param pageSize
	 * @param brand
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize,TbBrand brand);
	/**
	 * 下拉框数据
	 * @return
	 */
	public List<Map> selectOptionList();
	
}
