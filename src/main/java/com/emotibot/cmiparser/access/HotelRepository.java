package com.emotibot.cmiparser.access;

import com.emotibot.cmiparser.entity.po.Employee;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: zujikang
 * @Date: 2020-04-13 0:44
 */
public interface HotelRepository extends ElasticsearchRepository<HotelEntity,String> {

}
