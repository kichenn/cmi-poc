package com.emotibot.cmiparser.entity.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TimeParseRes {
    private String actType;
    private String retCode;
    private String timeUsed;
    private List<Inform> informs;


    @Data
    private static class Inform{
        private String name;
        private InnerVa value;
    }

    @Data
    private static class InnerVa{
        private String displayText;
        private String dataType;
        @JsonProperty("当前时间")
        private String currentTime;
        private Chrono chrono;
    }

    @Data
    private static class Chrono{
        private Duration duration;
    }
    @Data
    private static class Duration{

    }


}
