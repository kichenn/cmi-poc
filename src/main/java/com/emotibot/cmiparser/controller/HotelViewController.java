package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zujikang
 * @Date: 2020-04-12 20:23
 */
@RestController
@Slf4j
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class HotelViewController {

    @PostMapping("/hotelView")
    public Object hotelView(@RequestBody JSONObject generalInfo){
        //System.out.println(generalInfo);
        log.info("hotelView request: "+generalInfo);
        return generalInfo;
    }
}
