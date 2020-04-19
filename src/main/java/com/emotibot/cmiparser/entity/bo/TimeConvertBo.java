package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TimeConvertBo {
    private List<String> times;
    private long timeOffset;
}
