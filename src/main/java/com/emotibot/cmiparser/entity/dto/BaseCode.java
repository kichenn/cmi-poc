package com.emotibot.cmiparser.entity.dto;

/**
 * @Author: zujikang
 * @Date: 2020-04-08 19:08
 */
public enum  BaseCode {
    SUCCESS("success",0),
    SERVER_ERROR("server error",500),
    BAD_REQUEST("bad request",10001);



    private String msg;
    private Integer code;
    BaseCode(String msg,Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg(){
        return msg;
    }
    public Integer getCode(){
        return code;
    }


}
