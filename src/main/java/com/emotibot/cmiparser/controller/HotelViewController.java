package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.entity.dto.*;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.service.HotelViewService;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class HotelViewController {
    @Autowired
    private HotelViewService hotelViewService;
    //用户酒店列表缓存
    @Autowired
    private LoadingCache<String, UserCache> innerCache;

    @PostMapping("/hotelView")
    public SkillResponse hotelView(@RequestBody JSONObject slotInfo) {
        try {
            String userId = slotInfo.getString("userId");
            signUp(userId);
            log.info("userId:" + userId + "--hotelViewReq: " + slotInfo);
            UserQuery userQuery = getSlotInfo(slotInfo);

            ArrayList<Answer> answers = new ArrayList<>();
            List<HotelEntity> hotelEntities = hotelViewService.hotelView(userQuery, userId);
            if (CollectionUtils.isEmpty(hotelEntities)) {
                answers.add(Answer.builder().type("text").subType("text").value("抱歉，没有找到符合需求的酒店，请换个条件试试。").build());
            }else{
                answers.add(Answer.builder().type("text").subType("text").value("为你推荐如下优质酒店，可以跟我说你想订第几个：").build());
                answers.add(viewHandler(hotelEntities,userQuery));
            }

            SkillResponse result = SkillResponse.builder().skillStatus("3").answer(answers).build();
            log.info("response: " + JSONArray.toJSONString(result));
            return result;
        } catch (Exception e) {
           log.error(e.getMessage());
        }
        return null;
    }

    private Answer viewHandler(List<HotelEntity> hotelView, UserQuery userQuery) throws Exception {

        ArrayList<HotelCard> hotelCards = new ArrayList<>();
        for (HotelEntity hotelEntity : hotelView) {
            String roomType =userQuery.getRoomType();
            HotelCard hotelCard = HotelCard.build(hotelEntity, roomType);
            hotelCards.add(hotelCard);
        }

        return Answer.builder().type("card")
                .subType("text")
                .data(hotelCards)
                .build();
    }


    private UserQuery getSlotInfo(JSONObject slotInfo) {

        UserQuery userQuery = UserQuery.builder().build();
        try {

            if (!StringUtils.isEmpty(slotInfo.get("hotelName") )) {
                userQuery.setHotelName(slotInfo.getString("hotelName"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("roomType"))) {
                userQuery.setRoomType(slotInfo.getString("roomType"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("district"))) {
                userQuery.setDistrict(slotInfo.getString("district"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("ccity"))) {
                userQuery.setCcity(slotInfo.getString("ccity"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("specials"))) {
                userQuery.setSpecials(slotInfo.getString("specials"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("price"))) {
                userQuery.setPrice(slotInfo.getString("price"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("hotelStar"))) {
                userQuery.setHotelStars(slotInfo.getString("hotelStar"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("checkinTi"))) {
                userQuery.setCheckinTime(slotInfo.getString("checkinTi"));
            }
            if (!StringUtils.isEmpty(slotInfo.get("checkoutTi"))) {
                userQuery.setCheckoutTime(slotInfo.getString("checkoutTi"));
            }


        } catch (Exception e) {
        }

        return userQuery;
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
