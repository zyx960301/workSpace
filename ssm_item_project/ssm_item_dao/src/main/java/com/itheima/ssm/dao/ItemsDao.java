package com.itheima.ssm.dao;

import com.itheima.ssm.domain.Items;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ItemsDao {
    @Select("select * from items")
    List<Items> findAll();
//    @Insert("insert into INSERT INTO items VALUES (#{},#{},#{},#{},#{})")
//    void save(Items items);
}
