package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.bo.TimeConvertBo;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.entity.po.Kw;
import com.emotibot.cmiparser.util.ParserUtils;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 16:54
 */
@Service
public class SelectService {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;
    private DateFormat dateFormatFormat = new SimpleDateFormat("M月d日");

    public BaseResult parse(JSONObject generalInfo) {
        try {
            String text = generalInfo.getString("text");
            List<String> hotelList = null;
            String userId = ParserUtils.getUserId(generalInfo);
            if(StringUtils.isEmpty(userId)){
                //意图解析没有userId， 只有{text:dfsdf}
                ArrayList<String> strings = new ArrayList<>();
                strings.add("模拟酒店1");
                strings.add("模拟酒店2");
                strings.add("模拟酒店3");
                strings.add("模拟酒店4");
                hotelList = strings;
                String selectedHotelNameTmp = externalParser.selectParse(text, hotelList);
                if(StringUtils.isEmpty(selectedHotelNameTmp)){
                    return BaseResult.ok();
                }
                return BaseResult.ok(SlotResponse.build("selectedHo", selectedHotelNameTmp));
            }else {
                //有userId 正常对话
                signUp(userId);
                hotelList = innerCache.get(userId).getHotels().stream().map(e -> e.getEntity()).collect(Collectors.toList());
                String selectedHotelName = externalParser.selectParse(text, hotelList);
                if(StringUtils.isEmpty(selectedHotelName)){
                    return BaseResult.ok();
                }
                HotelEntity hotelEntity = innerCache.get(userId).getHotels().stream().filter(em -> em.getEntity().equals(selectedHotelName)).findFirst().get();
                return updateSlot("selectedHo",hotelEntity,userId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    private BaseResult updateSlot(String slotName, HotelEntity hotelEntity,String userId) throws Exception {
        if (hotelEntity == null) {
            return BaseResult.ok();
        }
        else{
            String slotValue = null;
            UserCache userCache = innerCache.get(userId);
            //拼接到槽位

            //入住时间，离开时间，房间类型必须有
            String district = "";
            Optional<Kw> temp = hotelEntity.getR_text().stream().filter(e -> e.getAttr().equals("区商圈")).findFirst();
            if(temp.isPresent()){
                district = temp.get().getValue();
            }

            //价格为$1200的尖沙咀香港半岛酒店的豪华间,入住时间是2020年4月5日，离店时间是2020年4月7日
            slotValue = "价格为$"+hotelEntity.getCurrentPrice()+"的"+district+hotelEntity.getEntity()+"的"
            +userCache.getRoomType()+",入住时间是"+dateFormatFormat.format(userCache.getCheckinTime())
            +"，离店时间是"+dateFormatFormat.format(userCache.getCheckoutTime());

            //清除此次会话缓存
            if(!StringUtils.isEmpty(slotValue)){
                innerCache.put(userId,UserCache.builder().build());
            }
            return BaseResult.ok(SlotResponse.build(slotName, slotValue));
        }

    }
}
