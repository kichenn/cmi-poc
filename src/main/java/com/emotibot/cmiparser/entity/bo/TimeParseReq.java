package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 12:04
 */
@Data
@Builder
public class TimeParseReq {
    private String id;
    private String query;
}

