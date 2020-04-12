package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.util.ParserUtils;
import com.emotibot.cmiparser.util.PreHandlingUnit;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Author: zujikang
 * @Date: 2020-04-10 1:11
 */
@Service
public class PriceService {

    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;

    public BaseResult parse(JSONObject generalInfo) {
        try {
            String userId = ParserUtils.getUserId(generalInfo);
            signUp(userId);

            String text = generalInfo.getString("text");
            text = PreHandlingUnit.numberTranslator(text);

            PriceParserBo priceParserBo = externalParser.priceParse(text);

            /*如有userID,存缓存*/
            if (userId != null && priceParserBo!=null) {
                innerCache.get(userId).setPriceParserBo(priceParserBo);
            }

            return updateSlot("price", priceParserBo);

        } catch (Exception e) {
            return BaseResult.ok();
        }
    }

    private BaseResult updateSlot(String slotName, PriceParserBo priceParserBo) {
        if (priceParserBo == null) {
            return BaseResult.ok();
        }
        else{
            List<String> entities = priceParserBo.getEntities();
            String slotValue = null;
            if (entities.size() >= 2) {
                String beginPrice = Integer.parseInt(entities.get(0)) < Integer.parseInt(entities.get(1)) ? entities.get(0) : entities.get(1);
                String endPrice = beginPrice.equals(entities.get(0)) ? entities.get(1) : entities.get(0);
                slotValue = beginPrice + "到" + endPrice + "元";
            } else if (entities.size() == 1) {
                switch (priceParserBo.getRange()) {
                    case "L":
                        slotValue = entities.get(0) + "元以下";
                        break;
                    case "R":
                        slotValue = entities.get(0) + "元以上";
                        break;
                    case "LR":
                        slotValue = entities.get(0) + "元左右";
                        break;
                    default:
                        slotValue = entities.get(0) + "元";
                        break;
                }
            }

            return BaseResult.ok(SlotResponse.build(slotName, slotValue));
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