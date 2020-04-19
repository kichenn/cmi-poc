package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PriceParserBo {
    private int amount;
    private int offset;
    private List<String> entities;
    private String range;
}
