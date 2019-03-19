package com.itheima.service;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if("getName".equals(method.getName())){
            System.out.println("before"+methodProxy.getSuperName());
            System.out.println(method.getName());
            Object result = methodProxy.invokeSuper(o,objects);
            System.out.println("after"+methodProxy.getSuperName());
            return result;
        }
        else if("getAge".equals(method.getName())) {
            Object result = methodProxy.invokeSuper(o,objects);
            return result;
        }
        else {
            Integer a = (Integer) objects[0];
            Object result = methodProxy.invokeSuper(o, a*2);
            System.out.println(result);
            return result;
        }
    }
}
