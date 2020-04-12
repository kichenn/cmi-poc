package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 17:01
 */
@Data
@Builder
public class UserCache {
    private Date checkinTime;
    private Date checkoutTime;
    private PriceParserBo priceParserBo;
    private List<String> hotelStars;
}
