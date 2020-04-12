package com.emotibot.cmiparser.entity.po;

import lombok.Data;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @Author: zujikang
 * @Date: 2020-04-13 3:14
 */
@Data
@Document(indexName = "emotibot_kg_product", type = "emotibot_skg_type")
public class HotelEntity {

    @Id
    @Field(type = FieldType.Text)
    private String id;

//    //设置字段名称及索引
//    @Field(type = FieldType.Keyword)
//    private String entity;
//
//    @Field(type = FieldType.Text)
//    private String app_id;
//
//    @Field(type = FieldType.Nested)
//    private List<Kw> r_kw;
//
//    @Field(type = FieldType.Nested)
//    private List<Kw> r_text;
//
//    @Field(type = FieldType.Nested)
//    private List<Myfloat> r_float;

    /*@Field(type = FieldType.Nested)
    private List<Object> r_date;
    @Field(type = FieldType.Nested)
    private List<Object> r_interval_float;
    @Field(type = FieldType.Text)
    private List<String> r_extra;*/

}


@Data
class Kw{
    private String attr;
    private String value;
}

@Data
class Myfloat{
    private String attr;
    private Double value;
    private String unit;
    private String value_show;
}
