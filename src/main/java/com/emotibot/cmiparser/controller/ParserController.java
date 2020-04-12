package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.service.CheckinService;
import com.emotibot.cmiparser.service.CheckoutService;
import com.emotibot.cmiparser.service.HotelStarService;
import com.emotibot.cmiparser.service.PriceService;
import com.emotibot.cmiparser.util.ParserUtils;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.net.www.ParseUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


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


    @PostMapping("/parser/checkinTime")
    public BaseResult checkinParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo,"checkinTime");
        BaseResult response = checkinService.parse(generalInfo);
        logRes(generalInfo,response);
        return response;
    }

    @PostMapping("/parser/checkoutTime")
    public BaseResult checkoutParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo,"checkoutTime");
        BaseResult response = checkoutService.parse(generalInfo);
        logRes(generalInfo,response);
        return response;
    }

    @PostMapping("/parser/price")
    public BaseResult priceParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo,"price");
        BaseResult response = priceService.parse(generalInfo);
        logRes(generalInfo,response);
        return response;
    }

    @PostMapping("/parser/hotelStar")
    public BaseResult hotelStarParser(@RequestBody JSONObject generalInfo) {
        logReq(generalInfo,"hotelStar");
        BaseResult response = hotelStarService.parse(generalInfo);
        logRes(generalInfo,response);
        return response;
    }


    private void logReq(JSONObject generalInfo, String inf) {
        log.info("interface: " + inf + "--" + generalInfo.toString());
        log.info("userId: "+ ParserUtils.getUserId(generalInfo));
    }

    private void logRes(JSONObject generalInfo,Object result){
        log.info("response: "+result);
        try {
            log.info("userCache: "+innerCache.get(ParserUtils.getUserId(generalInfo)));
        } catch (ExecutionException e) {
//            e.printStackTrace();
        }
    }



}
