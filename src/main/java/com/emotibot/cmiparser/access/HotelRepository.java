package com.emotibot.cmiparser.access;

import com.emotibot.cmiparser.entity.po.HotelEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: zujikang
 * @Date: 2020-04-13 0:44
 */
public interface HotelRepository extends ElasticsearchRepository<HotelEntity,String> {

}
