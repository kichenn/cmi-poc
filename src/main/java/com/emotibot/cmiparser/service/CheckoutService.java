package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.TimeConvertBo;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.util.ParserUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-10 18:21
 */
@Service
public class CheckoutService {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;
    @Value("${dialogueBeh}")
    private String dialogueBeh;
    private DateFormat dateFormatFormat = new SimpleDateFormat("M月d日");


    public BaseResult parse(JSONObject generalInfo) {
        try {

            String userId = ParserUtils.getUserId(generalInfo);
            signUp(userId);

            String text = generalInfo.getString("text");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            TimeConvertBo timeConvertBo = externalParser.timeParse(text);
            if (timeConvertBo == null) {
                return null;
            }

            /*槽位转换逻辑*/
            //两个时间  取后一个入槽
            List<String> times = timeConvertBo.getTimes();
            long timeOffset = timeConvertBo.getTimeOffset();
            Date checkoutTime  = null;
            if (times.size() >= 2 || (times.size() == 1 && timeOffset != 0L) ) {
                if(timeOffset != 0L) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateFormat.parse(times.get(0)));
                    calendar.add(Calendar.DATE,(int)timeOffset);
                    checkoutTime = calendar.getTime();
                }else{
                    checkoutTime = dateFormat.parse(times.get(1));
                }

            }

            // 一个时间  判断有指定上文则入槽
            else if (times.size() == 1 && timeOffset == 0L) {
                if(generalInfo.get("task_info")!=null && generalInfo.getJSONObject("task_info").get("sys_chat_history")!=null) {
                    JSONArray jsonArray = generalInfo.getJSONObject("task_info").getJSONArray("sys_chat_history");
                    String bot = jsonArray.getJSONObject(jsonArray.size() - 1).getString("bot");
                    String s = bot.replaceAll("\\\"", "");
                    String diaBeh = s.substring(s.indexOf("payload") + "payload".length()+1, s.length()-1);

                    if(diaBeh.equals(dialogueBeh)) {
                        checkoutTime = dateFormat.parse(times.get(0));
                    }
                }
            }

            /*如有userID,存缓存*/
            if(userId != null && checkoutTime != null) {
                innerCache.get(userId).setCheckoutTime(checkoutTime);
            }

            return updateSlot("checkoutTi",checkoutTime);

        } catch (Exception e) {
            return BaseResult.ok();
        }
    }

    private BaseResult updateSlot(String slotName, Date slotValue) {
        if(slotValue != null) {
            return BaseResult.ok(SlotResponse.build(slotName, dateFormatFormat.format(slotValue)));
        }else {
            return BaseResult.ok();
        }
    }

    private void signUp(String userId) {
        try {
            innerCache.get(userId);
        } catch (Exception e) {
            if (e instanceof CacheLoader.InvalidCacheLoadException) {
                innerCache.put(userId, UserCache.builder().build());
            }
        }
    }
}
