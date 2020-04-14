package com.emotibot.cmiparser.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Answer {
    private String type;
    private String subType;
    private String value;
    private Object data;
}