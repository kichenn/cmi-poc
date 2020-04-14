package com.emotibot.cmiparser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotelStarUnit {

    public static int parseHotelStar(String Hotel_Star_Expression){
        if(Hotel_Star_Expression == null){
            return 0;
        }
        int Hotel_Star = 0;
        String rule="(\\d+)(?=(星|级|星级))";
        Pattern pattern=Pattern.compile(rule);
        Matcher match=pattern.matcher(Hotel_Star_Expression);
        if(match.find())
        {
            Hotel_Star = Integer.valueOf(match.group());
            Hotel_Star = (Hotel_Star > 5 || Hotel_Star < 1) ? 5 : Hotel_Star;
            System.out.println("parser Hotel Star:" + Hotel_Star);
        }

        return Hotel_Star;
    }

}