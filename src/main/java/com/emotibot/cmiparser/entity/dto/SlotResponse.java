package com.emotibot.cmiparser.entity.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlotResponse {
    private JSONObject update;

    public static SlotResponse build(String slotName, String slotValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(slotName,slotValue);
        return SlotResponse.builder().update(jsonObject).build();
    }
}
