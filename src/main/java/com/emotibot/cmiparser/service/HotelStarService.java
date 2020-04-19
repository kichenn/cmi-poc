package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.entity.dto.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HotelStarService {
    @Autowired
    private ExternalParser externalParser;

    public BaseResult parse(JSONObject generalInfo){
        try {

            String text = generalInfo.getString("text");
            if(text.contains("星")) {
                List<String> selects = externalParser.hotelStarParse(text);
                if (selects != null) {
                    return updateSlot("hotelStar", selects);
                }
            }

            return BaseResult.ok();
        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseResult.ok();
        }

    }

    private BaseResult updateSlot(String slotName, List<String> selects) {
        if(selects==null) {
            return BaseResult.ok();
        }else {

            return BaseResult.ok(SlotResponse.build(slotName,String.join(",",selects)));
        }
    }


}
