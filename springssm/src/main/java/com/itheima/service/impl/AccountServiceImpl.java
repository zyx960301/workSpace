package com.itheima.service.impl;

import com.itheima.dao.AccountMapper;
import com.itheima.domain.Account;
import com.itheima.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Override
    public List<Account> findAll() {
        return accountMapper.findAll();
    }

    @Override
    public int saveAccount(Account account) {
        return accountMapper.saveAccount(account);
    }

    @Override
    public Account findAccountById(Integer integer) {
        return accountMapper.findAccountById(integer);
    }

    @Override
    public int updateAccount(Account account) {
        return accountMapper.updateAccount(account);
    }
}
