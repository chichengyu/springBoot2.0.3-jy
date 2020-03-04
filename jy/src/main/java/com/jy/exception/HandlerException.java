package com.jy.exception;


import com.jy.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 */
@RestControllerAdvice
public class HandlerException {

    private static Logger logger = LoggerFactory.getLogger(HandlerException.class);

    /**
     * 捕获 Exception
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Response<String> handler(Exception e){
        if (e instanceof BaseException){
            // BaseException
            BaseException exception = (BaseException) e;
            return Response.error(exception.getCode(),exception.getMessage());
        }else{
            logger.error("【系统异常】，{}",e);
            // Exception
            return Response.error(e.getMessage());
        }
    }
}
