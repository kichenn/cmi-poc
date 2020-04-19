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

@Component
public class ExternalParser {

    @Value("${external.parser.url}")
    private String parserUrl;
    @Value("${external.snlu}")
    private String snluParseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public TimeConvertBo timeParse(String text) {
        TimeParseReq body = TimeParseReq.builder().id("chrono").query(text).build();
        JSONObject result = doPost(parserUrl, body);

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
            return null;
        }

    }


    public PriceParserBo priceParse(String text) {
        PriceParseReq body = PriceParseReq.builder().id("money").hasContext(true).query(text).build();
        JSONObject result = doPost(parserUrl, body);

        try {

            JSONObject money = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("money");

            int amount = money.get("amount") != null ? (int) money.get("amount") : 0;
            int offset = money.get("offset") != null ? (int) money.get("offset") : 0;
            String range = money.get("range") != null ? (String) money.get("range") : "";

            List<String> entities = null;
            if(money.get("entities")!=null) {
                entities  = money.getJSONArray("entities").stream().map(e -> (String) e).collect(Collectors.toList());
            }else {
                entities = new ArrayList<>();
                String s = Integer.toString(amount);
                entities.add(s.substring(0,s.length()-2));
            }

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
        HotelStarReq body = new HotelStarReq();
        body.setQuery(text);
        JSONObject result = doPost(parserUrl, body);

        try {
            JSONArray jsonArray = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("option")
                    .getJSONArray("selects");

            List<String> selects = jsonArray.stream().map(e -> (String) e).collect(Collectors.toList());
            return selects;
        } catch (Exception e) {
            return null;
        }

    }


    public String selectParse(String text,List<String> hotelList) {
        HotelStarReq body = new HotelStarReq(hotelList);
        body.setQuery(text);
        JSONObject result = doPost(parserUrl, body);

        try {
            JSONArray jsonArray = result.getJSONArray("informs")
                    .getJSONObject(0)
                    .getJSONObject("value")
                    .getJSONObject("option")
                    .getJSONArray("selects");

            List<String> selects = jsonArray.stream().map(e -> (String) e).collect(Collectors.toList());
            return selects.get(0);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 外部分词处理
     * @param question
     * @return
     */
    public String snluParse(String question) {

        JSONArray result = restTemplate.getForObject(snluParseUrl + "?f=segment&q=" + question, JSONArray.class);
        String answer = question;

        try {

            JSONArray segment = result.getJSONObject(0).getJSONArray("segment");
            int size = segment.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = segment.getJSONObject(i);
                String word = jsonObject.getString("word");
                answer = answer+"|"+word;
            }

            return answer;
        } catch (Exception e) {
            return answer;
        }

    }

    public <T> JSONObject doPost(String url, T body){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity<T>(body, headers);
        return restTemplate.postForObject(url, entity, JSONObject.class);
    }


}
