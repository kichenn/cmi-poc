package com.emotibot.cmiparser.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: zujikang
 * @Date: 2020-04-12 1:34
 */
public class ParserUtils {


    public static String getUserId(JSONObject generalInfo) {
        String userId = null;
//        if (generalInfo.get("task_info") != null) {
//            userId = generalInfo.getJSONObject("task_info").getString("sys_current_user");
//        }
        if(generalInfo.get("user_id") != null ){
            userId = generalInfo.getString("user_id").substring(0,32);
        }
        return userId;
    }
}
