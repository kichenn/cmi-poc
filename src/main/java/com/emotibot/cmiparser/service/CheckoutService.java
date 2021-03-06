package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.ExternalParser;
import com.emotibot.cmiparser.entity.bo.TimeConvertBo;
import com.emotibot.cmiparser.entity.dto.BaseResult;
import com.emotibot.cmiparser.entity.dto.SlotResponse;
import com.emotibot.cmiparser.util.ParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CheckoutService {
    @Autowired
    private ExternalParser externalParser;
    @Value("${dialogueBeh}")
    private String dialogueBeh;


    public BaseResult parse(JSONObject generalInfo) {
        try {

            String text = generalInfo.getString("text");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            TimeConvertBo timeConvertBo = externalParser.timeParse(text);
            if (timeConvertBo == null) {
                return BaseResult.ok();
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

            return checkoutTime != null ? updateSlot("checkoutTi",checkoutTime) : BaseResult.ok();


        } catch (Exception e) {
            log.error(e.getMessage());
            return BaseResult.ok();
        }
    }

    private BaseResult updateSlot(String slotName, Date slotValue) throws Exception{
        if(slotValue != null) {
            return BaseResult.ok(SlotResponse.build(slotName, ParserUtils.format(slotValue)));
        }else {
            return BaseResult.ok();
        }
    }

}
