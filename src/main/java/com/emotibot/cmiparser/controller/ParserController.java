package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.entity.dto.BaseResult;
import com.emotibot.cmiparser.service.*;
import com.emotibot.cmiparser.util.ParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class ParserController {

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
        log.info("userId:" + ParserUtils.getUserId(generalInfo)+"--checkinTimeReq: "+generalInfo);
        BaseResult response = checkinService.parse(generalInfo);
        log.info("response: " + JSONArray.toJSONString(response));
        return response;
    }

    @PostMapping("/parser/checkoutTime")
    public BaseResult checkoutParser(@RequestBody JSONObject generalInfo) {
        log.info("userId:" + ParserUtils.getUserId(generalInfo)+"--checkoutTimeReq: "+generalInfo);
        BaseResult response = checkoutService.parse(generalInfo);
        log.info("response: " + JSONArray.toJSONString(response));
        return response;
    }

    @PostMapping("/parser/price")
    public BaseResult priceParser(@RequestBody JSONObject generalInfo) {
        log.info("userId:" + ParserUtils.getUserId(generalInfo)+"--priceReq: "+generalInfo);
        BaseResult response = priceService.parse(generalInfo);
        log.info("response: " + JSONArray.toJSONString(response));
        return response;
    }

    @PostMapping("/parser/hotelStar")
    public BaseResult hotelStarParser(@RequestBody JSONObject generalInfo) {
        log.info("userId:" + ParserUtils.getUserId(generalInfo)+"--hotelStarReq: "+generalInfo);
        BaseResult response = hotelStarService.parse(generalInfo);
        log.info("response: " + JSONArray.toJSONString(response));
        return response;
    }

    @PostMapping("/parser/selectParse")
    public BaseResult selectParser(@RequestBody JSONObject generalInfo) {
        log.info("userId:" + ParserUtils.getUserId(generalInfo)+"--selectParseReq: "+generalInfo);
        BaseResult response = selectService.parse(generalInfo);
        log.info("response: " + JSONArray.toJSONString(response));
        return response;
    }


}
