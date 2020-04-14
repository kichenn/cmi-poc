package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.po.HotelEntity;
import lombok.Builder;
import lombok.Data;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020/4/13 13:54
 */
@Data
@Builder
public class SkillResponse {
//    private String skillStatus = "3";
//    private Answer answer;
//    private String userId;
    private String type;
    private String subType;
    private String value;
    private List<HotelCard> data;
}
