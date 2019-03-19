package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.Service;

public class Serviceimpl implements Service {
    UserDao dao = new UserDaoImpl();

    /**
     * 检查用户名
     * @param username
     * @return
     */
    @Override
    public Boolean checkname(String username) {
        Boolean flag = null;
        User user = dao.checkname(username);
        if(user == null){
            flag = false;
        }
        else {
            flag = true;
        }
        return flag;
    }

    /**
     * 保存user
     * @param user
     * @return
     */
    @Override
    public Boolean save(User user) {
        Boolean flag;
        //保存用户
        dao.save(user);
        //检查是否保存用户
       User user1 = dao.checkname(user.getUsername());
        if(user1!=null){
            flag = true;
        }
        else {
            flag = false;
        }
        return flag;
    }
}
