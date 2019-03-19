package com.itheima.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        MyInvocationHandler myInvocationHandler = new MyInvocationHandler(userService);
        /**
         * 参数1：加载类
         * 参数2：与被代理对象具有相同的方法
         * 参数3: 提供增强的代码
         */
        UserService proxyUserService = (UserService) Proxy.newProxyInstance(userService.getClass().getClassLoader(),userService.getClass().getInterfaces(),myInvocationHandler);
        System.out.println( proxyUserService.getName(1));
        System.out.println(proxyUserService.getAge(1));
        System.out.println(proxyUserService.getMoney(1000));

    }
}
