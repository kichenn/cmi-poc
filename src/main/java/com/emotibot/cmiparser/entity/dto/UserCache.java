package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.scheduling.support.SimpleTriggerContext;

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
    private String specials;
    private String ccity;
    private String district;
    private String hotelName;
    private String roomType;

    private List<HotelEntity> hotels;



}
