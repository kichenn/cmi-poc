package com.emotibot.cmiparser.entity.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 17:16
 */
@Data
@Builder
public class TimeConvertBo {
    private List<String> times;
    private long timeOffset;
}
