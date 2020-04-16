package com.emotibot.cmiparser;

import com.alibaba.fastjson.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class CmiParserApplicationTests {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("haha","heihei");
//
//        jsonObject.remove("haha");
//        JSONObject haha = jsonObject.getJSONObject("haha");
//        System.out.println(haha);
//
//        JSONObject heihei = haha.getJSONObject("heihei");
//        System.out.println(heihei);
//        ParserUtils.hotelStarParse(null);

      /*  HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
////        map.add("f","segment");
////        map.add("q","我要香港米娜酒店");
//        map.add("f","segment");
//        map.add("q","我要香港米娜酒店");
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
//
////        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://www.baidu.com", String.class, objectObjectHashMap);
        JSONArray forObject = restTemplate.getForObject("http://172.16.102.120:13901?f=segment&q=我要香港米娜酒店", JSONArray.class);*/

//        ResponseEntity<SnluRes> exchange = restTemplate.exchange("http://172.16.102.120:13901?f=segment&q=我要香港米娜酒店", HttpMethod.GET,  entity,SnluRes.class);



//        String url="http://www.baidu.com";
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("id","hahak");
//        HttpHeaders header = new HttpHeaders();
//        // 需求需要传参为form-data格式
//        header.setContentType(MediaType.MULTIPART_FORM_DATA);
//        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, header);
////        restTemplate.getForObject(url, httpEntity, JSONObject.class);
//        ResponseEntity<JSONObject> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);


        String s = "香港喜来登酒店标准间";
        String s1 = "标准间";
        boolean haha = s.contains("标准间");


    }

}
