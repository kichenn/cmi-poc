package com.emotibot.cmiparser.entity.po;

import lombok.Data;
import lombok.ToString;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Document(indexName = "emotibot_kg_product", type = "emotibot_skg_type")
public class HotelEntity implements Comparable<HotelEntity>{

    @Id
    @Field(type = FieldType.Text)
    private String id;

    //设置字段名称及索引
    @Field(type = FieldType.Keyword)
    private String entity;

    @Field(type = FieldType.Text)
    private String app_id;

    @Field(type = FieldType.Nested)
    private List<Kw> r_kw;

    @Field(type = FieldType.Nested)
    private List<Kw> r_text;

    @Field(type = FieldType.Nested)
    private List<Myfloat> r_float;

    @Override
    public int compareTo(HotelEntity o) {
        Myfloat myfloat = this.getR_float().get(this.getR_float().size() - 1);
        Myfloat myfloat1 = o.getR_float().get(o.getR_float().size() - 1);
        return (myfloat1.getValue().compareTo( myfloat.getValue()));
    }


    private String roomArea;
    private Double currentPrice;

    private String checkinTime;
    private String checkoutTime;
    private String roomType;



}





