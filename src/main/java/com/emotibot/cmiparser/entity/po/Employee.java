package com.emotibot.cmiparser.entity.po;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

// indexName为索引名称,type为该索引下的类型
@Data
@Document(indexName = "megacorp", type = "employee")
public class Employee {

    @Id
    private Long id;
    //设置字段名称及索引
    @Field(type = FieldType.Text)
    private String about;

    @Field(type = FieldType.Long)
    private Long age;

    @Field(type = FieldType.Text)
    private String first_name;

    @Field(type = FieldType.Text)
    private String last_name;

    @Field(type = FieldType.Text)
    private List<String> interests;


}
