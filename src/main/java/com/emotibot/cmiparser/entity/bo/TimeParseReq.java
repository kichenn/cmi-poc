package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeParseReq {
    private String id;
    private String query;
}

