package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.HotelRepository;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.Answer;
import com.emotibot.cmiparser.entity.dto.HotelCard;
import com.emotibot.cmiparser.entity.dto.SkillResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.entity.po.Kw;
import com.emotibot.cmiparser.entity.po.Myfloat;
import com.emotibot.cmiparser.service.HotelViewService;
import com.emotibot.cmiparser.util.ParserUtils;
import com.emotibot.cmiparser.util.PreHandlingUnit;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationSupport;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zujikang
 * @Date: 2020-04-12 20:23
 */
@RestController
@Slf4j
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class HotelViewController {
    @Autowired
    private HotelViewService hotelViewService;
    @Autowired
    private LoadingCache<String, UserCache> innerCache;

    @PostMapping("/hotelView")
    public SkillResponse hotelView(@RequestBody JSONObject slotInfo) {
        try {
            String userId = slotInfo.getString("userId");
            signUp(userId);
            log.info("userId:" + userId+"--hotelViewReq:"+slotInfo);
            getSlotInfo(slotInfo);
            SkillResponse result = viewHandler(hotelViewService.hotelView(userId),userId);
            logRes(slotInfo,result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SkillResponse viewHandler(List<HotelEntity> hotelView,String userId) throws Exception{
        if(hotelView == null) {
            return null;
        }
        ArrayList<HotelCard> hotelCards = new ArrayList<>();
        for (HotelEntity hotelEntity : hotelView) {
            String roomType = innerCache.get(userId).getRoomType();
            HotelCard hotelCard = HotelCard.build(hotelEntity, roomType);
            hotelCards.add(hotelCard);
        }

        return SkillResponse.builder().type("card")
                .subType("text")
                .data(hotelCards)
//                .userId(userId)
                .build();
    }


    private void getSlotInfo(JSONObject slotInfo) {
        try {

            String userId = slotInfo.getString("userId");

            if (slotInfo.get("hotelName") != null) {
                innerCache.get(userId).setHotelName(slotInfo.getString("hotelName"));
            }
            if (slotInfo.get("roomType") != null) {
                innerCache.get(userId).setRoomType(slotInfo.getString("roomType"));
            }
            if (slotInfo.get("district") != null) {
                innerCache.get(userId).setDistrict(slotInfo.getString("district"));
            }
            if (slotInfo.get("ccity") != null) {
                innerCache.get(userId).setCcity(slotInfo.getString("ccity"));
            }
            if (slotInfo.get("specials") != null) {
                innerCache.get(userId).setSpecials(slotInfo.getString("specials"));


            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    private void logRes(JSONObject generalInfo, Object result) {
        log.info("response:" + result);
        try {
            log.info("userId:"+ParserUtils.getUserId(generalInfo)+"--userCache: " + innerCache.get(ParserUtils.getUserId(generalInfo)));
        } catch (Exception e) {
//            e.printStackTrace();
            log.debug(e.getMessage());
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
