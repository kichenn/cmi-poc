package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.entity.dto.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.entity.po.Kw;
import com.emotibot.cmiparser.util.ParserUtils;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SelectService {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;

    public BaseResult parse(JSONObject generalInfo) {
        try {
            String text = generalInfo.getString("text");
            List<String> hotelList = null;
            String userId = ParserUtils.getUserId(generalInfo);
            if (StringUtils.isEmpty(userId)) {
                return getVirtualData(text);
            }

            //有userId 正常对话
            List<String> hotelListTmp = innerCache.get(userId).getHotels().stream().map(e -> e.getEntity()).collect(Collectors.toList());
            //对酒店列表做分词处理   "香港喜来登酒店" -> "香港喜来登酒店|香港|喜来登|酒店"
            hotelList = hotelListTmp.stream().map(externalParser::snluParse).collect(Collectors.toList());
            String selectedHotelName = externalParser.selectParse(text, hotelList);

            if (StringUtils.isEmpty(selectedHotelName)) {
                return BaseResult.ok();
            }
            HotelEntity hotelEntity = innerCache.get(userId).getHotels().stream().filter(em -> em.getEntity().equals(selectedHotelName)).findFirst().get();
            return updateSlot("selectedHo", hotelEntity);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private BaseResult getVirtualData(String text) {
        //意图解析没有userId， 只有{text:dfsdf}
        List<String> hotelList;
        ArrayList<String> strings = new ArrayList<>();
        strings.add("模拟酒店1");
        strings.add("模拟酒店2");
        strings.add("模拟酒店3");
        strings.add("模拟酒店4");
        hotelList = strings;
        String selectedHotelNameTmp = externalParser.selectParse(text, hotelList);
        return StringUtils.isEmpty(selectedHotelNameTmp) ? BaseResult.ok() : BaseResult.ok(SlotResponse.build("selectedHo", selectedHotelNameTmp));
    }

    private BaseResult updateSlot(String slotName, HotelEntity hotelEntity) throws Exception {
        if (hotelEntity == null) {
            return BaseResult.ok();
        }

        //入住时间，离开时间，房间类型必须有
        Optional<Kw> temp = hotelEntity.getR_text().stream().filter(e -> e.getAttr().equals("区商圈")).findFirst();
        String district = temp.isPresent() ? temp.get().getValue() : "";

        //价格为$1200的尖沙咀香港半岛酒店的豪华间,入住时间是2020年4月5日，离店时间是2020年4月7日
        StringBuffer slotValue = new StringBuffer();
        slotValue.append("价格为$");
        slotValue.append(hotelEntity.getCurrentPrice());
        slotValue.append("的");
        slotValue.append(district + hotelEntity.getEntity());
        slotValue.append("的");
        slotValue.append(hotelEntity.getRoomType());
        slotValue.append("，入住时间是");
        slotValue.append(hotelEntity.getCheckinTime());
        slotValue.append("，离店时间是");
        slotValue.append(hotelEntity.getCheckoutTime());

        return BaseResult.ok(SlotResponse.build(slotName, slotValue.toString()));

    }
}
