package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.service.*;
import com.emotibot.cmiparser.util.ParserUtils;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: zujikang
 * @Date: 2020-04-08 19:11
 */
@Slf4j
@RestController
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class ParserController {

    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private CheckinService checkinService;
    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private HotelStarService hotelStarService;
    @Autowired
    private SelectService selectService;


    @PostMapping("/parser/checkinTime")
    public BaseResult checkinParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo, "checkinTime");
        BaseResult response = checkinService.parse(generalInfo);
        logRes(generalInfo, response);
        return response;
    }

    @PostMapping("/parser/checkoutTime")
    public BaseResult checkoutParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo, "checkoutTime");
        BaseResult response = checkoutService.parse(generalInfo);
        logRes(generalInfo, response);

        return response;
    }

    @PostMapping("/parser/price")
    public BaseResult priceParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo, "price");
        BaseResult response = priceService.parse(generalInfo);
        logRes(generalInfo, response);

        return response;
    }

    @PostMapping("/parser/hotelStar")
    public BaseResult hotelStarParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo, "hotelStar");
        BaseResult response = hotelStarService.parse(generalInfo);
        logRes(generalInfo, response);
        return response;
    }

    @PostMapping("/parser/selectParse")
    public BaseResult selectParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo, "selectParse");
        BaseResult response = selectService.parse(generalInfo);
//        logRes(generalInfo, response);


        log.info("selectParse response: " + response);
        try {
            log.info("selectParse userCache: " + innerCache.get(ParserUtils.getUserId(generalInfo)));
        } catch (Exception e) {
//            e.printStackTrace();
        }


        return response;

    }


    private void logReq(JSONObject generalInfo, String inf) {
        log.info("interface: " + inf + "--" + generalInfo.toString());
        log.info("userId: " + ParserUtils.getUserId(generalInfo));
    }

    private void logRes(JSONObject generalInfo, Object result) {
        log.info("response: " + result);
        try {
            log.info("userId: "+ParserUtils.getUserId(generalInfo)+"userCache: " + innerCache.get(ParserUtils.getUserId(generalInfo)));
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }




}
