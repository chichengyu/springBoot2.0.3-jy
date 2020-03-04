package com.jy.exception;

/**
 * 基类异常类
 */
public class BaseException extends RuntimeException {

    private int code;
    private String message;

    public BaseException(String message){
        super(message);
    }

    public BaseException(String message,int code){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
