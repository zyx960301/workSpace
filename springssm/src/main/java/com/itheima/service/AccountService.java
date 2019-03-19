package com.itheima.service;

import com.itheima.domain.Account;

import java.util.List;

public interface AccountService {
    /**
     * 查询所有
     * @return
     */
    List<Account> findAll();

    /**
     * 保存Account
     * @param account
     * @return
     */
    int saveAccount(Account account);

    /**
     * 根据account查
     * @param integer
     * @return
     */
    Account findAccountById(Integer integer);

    /**
     * 更新 account
     * @param account
     * @return
     */
    int updateAccount(Account account);
}
