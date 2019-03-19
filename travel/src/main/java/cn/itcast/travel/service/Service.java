package cn.itcast.travel.service;

import cn.itcast.travel.domain.User;

public interface Service {
    Boolean checkname(String username);

    Boolean save(User user);
}
