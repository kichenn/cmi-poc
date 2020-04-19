package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceParseReq {
    private String id;
    private Boolean hasContext;
    private String query;
}
