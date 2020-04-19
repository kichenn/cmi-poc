package com.emotibot.cmiparser.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    private static DateFormat dateFormat = new SimpleDateFormat("M月d日");


    public static String getUserId(JSONObject generalInfo) {
        String userId = null;
        if (generalInfo.get("task_info") != null) {
            userId = generalInfo.getJSONObject("task_info").getString("sys_current_user");
        }
        return userId;
    }

    public static List<Integer> hotelStarParse(String hotelStars) {

        ArrayList<Integer> result = new ArrayList<>();
        String s = PreHandlingUnit.numberTranslator(hotelStars);
        Pattern pattern = Pattern.compile("[1|2|3|4|5]");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String group = matcher.group();
            result.add(Integer.parseInt(group));
        }
        return result;
    }


    public static Date parse(String dateString) throws Exception {
        return dateFormat.parse(dateString);
    }

    public static String format(Date date) throws Exception {
        return dateFormat.format(date);
    }
}
