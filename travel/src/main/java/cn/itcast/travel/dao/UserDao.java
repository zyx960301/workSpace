package cn.itcast.travel.dao;

import cn.itcast.travel.domain.User;

public interface UserDao {
    User checkname(String username);

    void save(User user);
}
