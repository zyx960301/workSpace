package com.itheima.service;

public class UserServiceImpl implements UserService {
    public String getName(int id) {
        System.out.println("--getName--");
        return "Tom";
    }

    public Integer getAge(int id) {
        System.out.println("--getAge--");
        return 10;
    }

    public Integer getMoney(int money) {
        System.out.println(money);
        return money*2;
    }
}
