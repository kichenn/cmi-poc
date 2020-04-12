package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-10 0:47
 */
@Data
@Builder
public class PriceParserBo {
    private int amount;
    private int offset;
    private List<String> entities;
    private String range;
}
