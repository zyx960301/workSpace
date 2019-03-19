package com.itheima.test;

import com.itheima.controller.AccountController;
import com.itheima.dao.AccountMapper;
import com.itheima.domain.Account;
import com.itheima.service.AccountService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SpringTest {
    ApplicationContext ac = null;
    {
        ac = new ClassPathXmlApplicationContext("spring/*.xml");
    }

    /***
     * 测试数据源
     * @throws SQLException
     */
    @Test
    public void datasource() throws SQLException {
        DataSource dataSource = (DataSource) ac.getBean("dataSource");
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    /**
     *测试代理类
     */
    @Test
    public void testMapper(){
        AccountMapper accountmapper = (AccountMapper) ac.getBean("accountMapper");
//        List<Account> all = accountmapper.findAll();
//        System.out.println(all);
        Account account = new Account();
        account.setName("ddd");
        account.setMoney(1000f);
        account.setId(1);
//        int i = accountmapper.saveAccount(account);
//        Account account = accountmapper.findAccountById(1);
        int i = accountmapper.updateAccount(account);
        System.out.println(i);
    }
    @Test
    public void testService(){
        AccountService as = (AccountService) ac.getBean("accountServiceImpl");
        List<Account> list = as.findAll();
        System.out.println(list);
    }

    /**
     * 测试事务
     */
    @Test
    public void testTrans(){
        AccountService accountService = (AccountService) ac.getBean("accountServiceImpl");
        Account account1 = accountService.findAccountById(1);
        Account account2 = accountService.findAccountById(2);
        System.out.println(account1);
        System.out.println(account2);
        account1.setMoney(account1.getMoney()-100);
        accountService.updateAccount(account1);
        int a = 1/0;
        account2.setMoney(account2.getMoney()+100);
        accountService.updateAccount(account2);
    }
    @Test
    public void controllerTest(){
        AccountController accountController = (AccountController) ac.getBean(AccountController.class);
        ModelAndView all = accountController.findAll();
        System.out.println(all);
    }
}
