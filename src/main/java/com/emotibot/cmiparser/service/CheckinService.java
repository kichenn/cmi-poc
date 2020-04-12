package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.common.BaseResult;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
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
import java.util.Date;
import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-09 16:54
 */
@Service
public class CheckinService {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private ExternalParser externalParser;
    @Value("${dialoguePre}")
    private String dialoguePre;
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
            //两个时间  取前一个入槽
            List<String> times = timeConvertBo.getTimes();
            long timeOffset = timeConvertBo.getTimeOffset();
            Date checkinTime = null;
            if (times.size() >= 2 || (times.size() == 1 && timeOffset != 0L)) {
                checkinTime = dateFormat.parse(times.get(0));
            }

            // 一个时间  判断有指定上文则入槽
            else if (times.size() == 1 && timeOffset == 0L) {
                if (generalInfo.get("task_info") != null && generalInfo.getJSONObject("task_info").get("sys_chat_history") != null) {
                    JSONArray jsonArray = generalInfo.getJSONObject("task_info").getJSONArray("sys_chat_history");
                    String bot = jsonArray.getJSONObject(jsonArray.size() - 1).getString("bot");
                    String s = bot.replaceAll("\\\"", "");
                    String diaPre = s.substring(s.indexOf("payload") + "payload".length()+1, s.length()-1);

                    if (diaPre.equals(dialoguePre)) {
                        checkinTime = dateFormat.parse(times.get(0));
                    }
                }
            }

            /*如有userID,存缓存*/
            if (userId != null && checkinTime != null) {
                innerCache.get(userId).setCheckinTime(checkinTime);
            }

            return updateSlot("checkinTi", checkinTime);

        } catch (Exception e) {
            return BaseResult.ok();
        }
    }

    private BaseResult updateSlot(String slotName, Date slotValue) {
        if (slotValue != null) {
            return BaseResult.ok(SlotResponse.build(slotName, dateFormatFormat.format(slotValue)));
        } else {
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
   /* private BaseResult updateTimeSlot(String userId, String slotName) {

        String slotValue = null;

        try {


            if ("checkinTi".equals(slotName)) {
                Date date = innerCache.get(userId).getCheckinTime();
                slotValue = dateFormat.format(date);

            } else if ("checkoutTi".equals(slotName)) {
                Date date = innerCache.get(userId).getCheckoutTime();
                slotValue = dateFormat.format(date);

            } else if ("price".equals(slotName)) {
                //渲染数据
                PriceParserBo priceParserBo = innerCache.get(userId).getPriceParserBo();

                if (priceParserBo != null) {
                    List<String> entities = priceParserBo.getEntities();

                    if (entities.size() >= 2) {
                        String beginPrice = Integer.parseInt(entities.get(0)) < Integer.parseInt(entities.get(1)) ? entities.get(0) : entities.get(1);
                        String endPrice = beginPrice.equals(entities.get(0)) ? entities.get(1) : entities.get(0);
                        slotValue = beginPrice + "到" + endPrice + "元";
                    } else if (entities.size() == 1) {
                        switch (priceParserBo.getRange()) {
                            case "L":
                                slotValue = entities.get(0) + "元以下";
                                break;
                            case "R":
                                slotValue = entities.get(0) + "元以上";
                                break;
                            case "LR":
                                slotValue = entities.get(0) + "元左右";
                                break;
                            default:
                                slotValue = entities.get(0) + "元";
                                break;
                        }
                    }
                }

            } else if("hotelStar".equals(slotName)){
                List<String> hotelStars = innerCache.get(userId).getHotelStars();
                if(hotelStars!=null) {
                    slotValue = String.join(",",hotelStars);
                }
            }


        } catch (Exception e) {
            return BaseResult.ok();
        }

        if(slotValue == null){
            return BaseResult.ok();
        }
        return BaseResult.ok(SlotResponse.build(slotName, slotValue));
    }*/










    /*    public void parse2(String userId ,String text) {
        try {

            UserCache userCache = innerCache.get(userId);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


            TimeConvertBo timeConvertBo = externalParser.timeParse(text);
            if (timeConvertBo == null) {
                return;
            }

            // 两个时间  直接更新两槽   (a:今天入住，明天离开  b:今晚入住，住两晚)
            List<String> times = timeConvertBo.getTimes();
            long timeOffset = timeConvertBo.getTimeOffset();
            if (times.size() >= 2 || (times.size() == 1 && timeOffset != 0L) ) {

                Date firstTime = dateFormat.parse(times.get(0));
                Date secondTime = null;
                if(timeOffset != 0L) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(firstTime);
                    calendar.add(Calendar.DATE,(int)timeOffset);
                    secondTime = calendar.getTime();
                }else {
                    secondTime = dateFormat.parse(times.get(1));
                }

                if (firstTime.getTime() <= secondTime.getTime()) {
                    userCache.setCheckinTime(firstTime);
                    userCache.setCheckoutTime(secondTime);
                } else {
                    userCache.setCheckinTime(secondTime);
                    userCache.setCheckoutTime(firstTime);
                }
            }

            // 一个时间   判断先后
            else if (times.size() == 1 && timeOffset == 0L) {
                Date firstTime = dateFormat.parse(times.get(0));
                if (userCache.getCheckinTime() == null) {
                    userCache.setCheckinTime(firstTime);
                } else {
                    Date tmpTime = userCache.getCheckinTime();
                    if (firstTime.getTime() <= tmpTime.getTime()) {
                        userCache.setCheckinTime(firstTime);
                        userCache.setCheckoutTime(tmpTime);
                    } else {
                        userCache.setCheckinTime(tmpTime);
                        userCache.setCheckoutTime(firstTime);
                    }
                }
            }

            //入店和离店日期相同则推迟一天
            if(userCache.getCheckinTime() != null  &&  userCache.getCheckoutTime() != null
                    && userCache.getCheckinTime().compareTo(userCache.getCheckoutTime()) == 0 )   {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(userCache.getCheckoutTime());
                calendar.add(Calendar.DATE,1);
                userCache.setCheckoutTime(calendar.getTime());
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }*/
}
