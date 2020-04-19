package com.emotibot.cmiparser.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Answer {
    private String type;
    private String subType;
    private String value;
    private List<HotelCard> data;
}
