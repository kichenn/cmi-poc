package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserQuery {
    private String checkinTime;
    private String checkoutTime;
    private String price;
    private String hotelStars;
    private String specials;
    private String ccity;
    private String district;
    private String hotelName;
    private String roomType;

    private List<HotelEntity> hotels;



}
