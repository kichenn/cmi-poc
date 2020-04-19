package com.emotibot.cmiparser.entity.dto;

import com.emotibot.cmiparser.entity.po.HotelEntity;
import lombok.Data;

@Data
public class HotelCard {
    private String specials;
    private String hotelName;
    private String score;
    private String hotelStar;
    private String district;
    private String roomType;
    private String roomArea;
    private Double price;
    private String profile;

    public static HotelCard build(HotelEntity hotelEntity,String roomType){
        HotelCard hotelCard = new HotelCard();
        hotelCard.setHotelName(hotelEntity.getEntity());
        hotelEntity.getR_float().stream().forEach(e->{
            if(e.getAttr().equals("星级")) {
                hotelCard.setHotelStar(e.getValue_show());
            }
            if(e.getAttr().equals("无忧行特惠")) {
                hotelCard.setSpecials(e.getValue_show());
            }
            if(e.getAttr().equals("欢迎度")) {
                hotelCard.setScore(e.getValue_show());
            }
        });
        hotelEntity.getR_text().stream().forEach(e->{
            if(e.getAttr().equals("区商圈")){
                hotelCard.setDistrict(e.getValue());
            }
        });
        hotelEntity.getR_text().stream().forEach(e->{
            if(e.getAttr().equals("图片")){
                hotelCard.setProfile(e.getValue());
            }
        });

        hotelCard.setRoomType(roomType);
        hotelCard.setRoomArea(hotelEntity.getRoomArea());
        hotelCard.setPrice(hotelEntity.getCurrentPrice());
        return hotelCard;
    }



}
