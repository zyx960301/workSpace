package com.itheima.dao;

import com.itheima.domain.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountMapper {
    @Select("select * from account")
    List<Account> findAll();
    @Insert("INSERT INTO account(NAME,money) VALUES(#{name},#{money})")
    int saveAccount(Account account);
    @Select("select * from account where id = #{uid}")
    Account findAccountById(Integer integer);
    @Update("update account set name = #{name},money = #{money} where id = #{id}")
    int updateAccount(Account account);
}