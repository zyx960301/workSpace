package com.itheima.dao;

import com.itheima.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao {
    
    @Select("SELECT * FROM USER")
    List<User> findAll();
}
