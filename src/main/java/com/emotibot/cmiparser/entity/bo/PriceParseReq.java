package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 14:21
 */
@Data
@Builder
public class PriceParseReq {
    private String id;
    private Boolean hasContext;
    private String query;
}
