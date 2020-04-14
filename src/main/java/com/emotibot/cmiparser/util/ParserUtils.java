package com.emotibot.cmiparser.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: zujikang
 * @Date: 2020-04-12 1:34
 */
public class ParserUtils {


    public static String getUserId(JSONObject generalInfo) {
        String userId = null;
        if (generalInfo.get("task_info") != null) {
            userId = generalInfo.getJSONObject("task_info").getString("sys_current_user");
        }
//        if (generalInfo.get("user_id") != null) {
//            userId = generalInfo.getString("user_id").substring(0, 32);
//        }
        return userId;
    }

    public static List<Integer> hotelStarParse(List<String> hotelStars) {
        ArrayList<Integer> result = new ArrayList<>();
        String join = String.join("-", hotelStars);
        String s = PreHandlingUnit.numberTranslator(join);
        Pattern pattern = Pattern.compile("[1,2,3,4,5]");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            result.add(Integer.parseInt(matcher.group()));
        }
        return result;
    }
}
