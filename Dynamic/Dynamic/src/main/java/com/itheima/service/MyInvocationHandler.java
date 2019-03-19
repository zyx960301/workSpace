package com.itheima.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {
    private Object target;

    public MyInvocationHandler() {
        super();
    }

    public MyInvocationHandler(Object target) {
        super();
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("getName".equals(method.getName())){
            System.out.println("before"+method.getName());
            Object result = method.invoke(target, args);
            System.out.println("after"+method.getName());
            return result;
        }
        else if("getAge".equals(method.getName())) {
            Object result = method.invoke(target, args);
            return result;
        }
        else {
            Object result = method.invoke(target, (Integer)args[0]*2);
            System.out.println(result);
            return result;
        }

    }
}
