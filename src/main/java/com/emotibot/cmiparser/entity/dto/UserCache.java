package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.po.HotelEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCache {
    private List<HotelEntity> hotels;
}
