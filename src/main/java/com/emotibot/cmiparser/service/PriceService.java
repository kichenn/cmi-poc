package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.util.PreHandlingUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class PriceService {

    @Autowired
    private ExternalParser externalParser;

    public BaseResult parse(JSONObject generalInfo) {
        try {

            String text = generalInfo.getString("text");
            text = PreHandlingUnit.numberTranslator(text);
            PriceParserBo priceParserBo = externalParser.priceParse(text);
            if (priceParserBo != null) {
                return updateSlot("price", priceParserBo);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return BaseResult.ok();
    }

    private BaseResult updateSlot(String slotName, PriceParserBo priceParserBo) {
        if (priceParserBo == null) {
            return BaseResult.ok();
        }
        //"不要3星级的" "找一下3-5元的" : 个位数不需要price解析
        List<String> entities1 = priceParserBo.getEntities();
        if (entities1.stream().filter(e -> e.length() < 2).count() == entities1.size()) {
            return BaseResult.ok();
        }

        List<String> entities = priceParserBo.getEntities();
        String slotValue = null;
        if (entities.size() >= 2) {
            String beginPrice = Integer.parseInt(entities.get(0)) < Integer.parseInt(entities.get(1)) ? entities.get(0) : entities.get(1);
            String endPrice = beginPrice.equals(entities.get(0)) ? entities.get(1) : entities.get(0);
            beginPrice = PreHandlingUnit.numberTranslator(beginPrice);
            endPrice = PreHandlingUnit.numberTranslator(endPrice);
            slotValue = beginPrice + "-" + endPrice;
        } else if (entities.size() == 1) {
            switch (priceParserBo.getRange()) {
                case "L":
                    slotValue = PreHandlingUnit.numberTranslator(entities.get(0)) + "L";
                    break;
                case "R":
                    slotValue = PreHandlingUnit.numberTranslator(entities.get(0)) + "R";
                    break;
                case "LR":
                    slotValue = PreHandlingUnit.numberTranslator(entities.get(0)) + "LR";
                    break;
                default:
                    slotValue = PreHandlingUnit.numberTranslator(entities.get(0));
                    break;
            }
        } else {
            return BaseResult.ok();
        }

        return BaseResult.ok(SlotResponse.build(slotName, slotValue));


    }


}