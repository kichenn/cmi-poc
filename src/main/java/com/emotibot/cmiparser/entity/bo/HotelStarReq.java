package com.emotibot.cmiparser.entity.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-10 11:56
 */
@Data
public class HotelStarReq {
    private String id;
    private boolean hasContext;
    private Argument arguments;
    private String query;

    public HotelStarReq(){
        id = "option";
        hasContext = true;
        arguments = new Argument();
    }

}

@Data
class Argument{
    private List<String> candidates;
    public Argument(){
        ArrayList<String> objects = new ArrayList<>();
        objects.add("一星级|1星");
        objects.add("二星级|2星");
        objects.add("三星级|3星");
        objects.add("四星级|4星");
        objects.add("五星级|5星");
        candidates = objects;
    }
}