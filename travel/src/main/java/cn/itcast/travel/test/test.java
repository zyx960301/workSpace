package cn.itcast.travel.test;

import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.impl.Serviceimpl;
import cn.itcast.travel.web.servlet.UserServlet;
import org.junit.Test;
public class test {
    @Test
    public void testcheckname(){
        Serviceimpl serviceimpl = new Serviceimpl();
        Boolean zhangsan = serviceimpl.checkname("zhangsan");
        System.out.println(zhangsan);
    }
    @Test
    public void testchecksave(){
        User user = new User();
        user.setPassword("123");
        user.setUsername("21q3123123");
        UserDaoImpl userDao = new UserDaoImpl();
        userDao.save(user);
    }
}
