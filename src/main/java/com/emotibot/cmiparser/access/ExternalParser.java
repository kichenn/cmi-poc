package com.emotibot.cmiparser.access;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.entity.bo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author: zujikang
 * @Date: 2020-04-09 10:28
 */
@Component
public class ExternalParser {

    @Value("${external.parser.url}")
    private String parserUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TimeConvertBo timeParse(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        TimeParseReq body = TimeParseReq.builder().id("chrono").query(text).build();
        HttpEntity<TimeParseReq> entity = new HttpEntity<>(body, headers);
        JSONObject result = restTemplate.postForObject(parserUrl, entity, JSONObject.class);

        try {

            JSONObject chrono = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("chrono");

            JSONArray jsonArray = chrono.getJSONObject("time").getJSONArray("items");

            ArrayList<String> times = new ArrayList<>();
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                String single = jsonArray.getJSONObject(i).getJSONObject("ISO_DATE").getString("single");
                times.add(single);
            }

            Long timeOffset = 0L;
            JSONObject duration = chrono.getJSONObject("duration");
            if (duration != null) {
                if (duration.getJSONArray("items") != null) {
                    JSONObject item = duration.getJSONArray("items").getJSONObject(0);
                    if (!StringUtils.isEmpty(item.getString("days"))) {
                        timeOffset = item.getLong("days");
                    }
                    if (!StringUtils.isEmpty(item.getString("nights"))) {
                        timeOffset = item.getLong("nights");
                    }
                }
            }

            return TimeConvertBo.builder().times(times).timeOffset(timeOffset).build();


        } catch (NullPointerException e) {
//            e.printStackTrace();
            return null;
        }

    }


    public PriceParserBo priceParse(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        PriceParseReq body = PriceParseReq.builder().id("money").hasContext(true).query(text).build();
        HttpEntity<PriceParseReq> entity = new HttpEntity<>(body, headers);
        JSONObject result = restTemplate.postForObject(parserUrl, entity, JSONObject.class);

        try {

            JSONObject money = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("money");

            int amount = money.get("amount") != null ? (int) money.get("amount") : 0;
            int offset = money.get("offset") != null ? (int) money.get("offset") : 0;
            String range = money.get("range") != null ? (String) money.get("range") : "";
            List<String> entities = money.getJSONArray("entities").stream().map(e -> (String) e).collect(Collectors.toList());

            return PriceParserBo.builder()
                    .amount(amount)
                    .offset(offset)
                    .entities(entities)
                    .range(range)
                    .build();


        } catch (NullPointerException e) {
            return null;
        }
    }

    public List<String> hotelStarParse(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HotelStarReq body = new HotelStarReq();
        body.setQuery(text);
        HttpEntity<HotelStarReq> entity = new HttpEntity<>(body, headers);
        JSONObject result = restTemplate.postForObject(parserUrl, entity, JSONObject.class);

        try {
            JSONArray jsonArray = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("option")
                    .getJSONArray("selects");

            List<String> selects = jsonArray.stream().map(e -> (String) e).collect(Collectors.toList());
            return selects;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }

    }



    public String selectParse(String text,List<String> hotelList) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HotelStarReq body = new HotelStarReq(hotelList);
        body.setQuery(text);
        HttpEntity<HotelStarReq> entity = new HttpEntity<>(body, headers);
        JSONObject result = restTemplate.postForObject(parserUrl, entity, JSONObject.class);

        try {
            JSONArray jsonArray = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("option")
                    .getJSONArray("selects");

            List<String> selects = jsonArray.stream().map(e -> (String) e).collect(Collectors.toList());
            return selects.get(0);
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }

    }


}
