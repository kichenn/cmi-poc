package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.util.ParserUtils;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: zujikang
 * @Date: 2020-04-10 12:41
 */
@Service
public class HotelStarService {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;

    public BaseResult parse(JSONObject generalInfo){
        try {
            String userId = ParserUtils.getUserId(generalInfo);
            signUp(userId);

            String text = generalInfo.getString("text");

            List<String> selects = externalParser.hotelStarParse(text);
            if(userId!=null && selects != null){
                innerCache.get(userId).setHotelStars(selects);
            }
            return updateSlot("hotelStar", selects);
        } catch (ExecutionException e) {
//            e.printStackTrace();
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

    private void signUp(String userId) {
        try {
            innerCache.get(userId);
        } catch (Exception e) {
            if (e instanceof CacheLoader.InvalidCacheLoadException) {
                innerCache.put(userId, UserCache.builder().build());
            }
        }
    }
}
