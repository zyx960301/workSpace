package com.tensquare.base.web.common;

import constants.StatusCode;
import dto.ResultDTO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类(通知类)
 */
//@ControllerAdvice
//@ResponseBody
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 异常时调用的方法
     * @param e
     * @return
     */
    @ExceptionHandler//默认情况下捕捉任何异常
    //也可以指定捕捉的异常
//    @ExceptionHandler(Throwable.class)
//    @ExceptionHandler(Exception.class)
    public ResultDTO error(Exception e){
        //控制台打印，记录日志
        e.printStackTrace();
        return new ResultDTO(false, StatusCode.ERROR,e.getMessage());
    }
}
