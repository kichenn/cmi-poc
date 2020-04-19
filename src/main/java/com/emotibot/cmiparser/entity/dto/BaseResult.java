package com.emotibot.cmiparser.entity.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: zujikang
 * @Date: 2020-04-08 18:57
 */
@Data
@Builder
public class BaseResult {
    private String parse_result;
    private Integer error_code;
    private Object msg_response;

    public static BaseResult ok() {
        return BaseResult.builder()
                .parse_result(BaseCode.SUCCESS.getMsg())
                .error_code(BaseCode.SUCCESS.getCode())
                .build();
    }

    public static BaseResult ok(Object msg_response) {
        return BaseResult.builder()
                .parse_result(BaseCode.SUCCESS.getMsg())
                .error_code(BaseCode.SUCCESS.getCode())
                .msg_response(msg_response)
                .build();
    }

    public static BaseResult error() {
        return BaseResult.builder()
                .parse_result(BaseCode.SERVER_ERROR.getMsg())
                .error_code(BaseCode.SERVER_ERROR.getCode())
                .build();
    }

    public static BaseResult wrap(BaseCode baseCode, Object msg_response) {
        return BaseResult.builder()
                .parse_result(baseCode.getMsg())
                .error_code(baseCode.getCode())
                .msg_response(msg_response)
                .build();
    }

    public static BaseResult wrap(BaseCode baseCode) {
        return BaseResult.builder()
                .parse_result(baseCode.getMsg())
                .error_code(baseCode.getCode())
                .build();
    }

}
