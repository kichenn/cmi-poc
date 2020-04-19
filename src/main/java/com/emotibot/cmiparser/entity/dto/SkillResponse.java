package com.emotibot.cmiparser.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SkillResponse {
    private String  skillStatus;
    private List<Answer> answer;

}
