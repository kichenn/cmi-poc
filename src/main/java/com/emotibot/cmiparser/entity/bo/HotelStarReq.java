package com.emotibot.cmiparser.entity.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    public HotelStarReq(List<String> strs){
        id = "option";
        hasContext = true;
        arguments = new Argument(strs);
    }

}

@Data
class Argument{
    private List<String> candidates;
    public Argument(){
        ArrayList<String> objects = new ArrayList<>();
        objects.add("一星级|一星|1星|1星级");
        objects.add("二星级|二星|2星|2星级");
        objects.add("三星级|三星|3星|3星级");
        objects.add("四星级|四星|4星|4星级");
        objects.add("五星级|五星|5星|5星级");
        candidates = objects;
    }

    public Argument(List<String> strs){
        candidates = strs;
    }
}