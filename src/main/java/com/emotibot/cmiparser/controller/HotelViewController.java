package com.emotibot.cmiparser.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.HotelRepository;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.Employee;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.util.ParserUtils;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @Author: zujikang
 * @Date: 2020-04-12 20:23
 */
@RestController
@Slf4j
@RequestMapping(value = "/cmi", produces = "application/json;charset=UTF-8")
public class HotelViewController {
    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private HotelRepository hotelRepository;

    @PostMapping("/hotelView")
    public Object hotelView(@RequestBody JSONObject generalInfo){
        try {
            //System.out.println(generalInfo);
            log.info("hotelView request: "+generalInfo);

            String userId = ParserUtils.getUserId(generalInfo);
//            UserCache userCache = innerCache.get(userId);

//            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("first_name", "Jane");
//
////        List<String> properties = new ArrayList<>();
////        Sort sort = Sort.by(Sort.Direction.DESC,"createDate");//排序
//
//            //多条件排序
////    Sort.Order order1 = new Sort.Order(Sort.Direction.DESC,"userId");
////    Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"userId");
////    Sort sortList = Sort.by(order1,order2);
//
//            //Pageable pageable = PageRequest.of(0, 10, sort);
//            Page<Employee> search = employeeRepository.search(matchQueryBuilder, pageable);
//            List<Employee> collect = search.get().collect(Collectors.toList());
//            System.out.println(collect);
            Iterable<HotelEntity> all = hotelRepository.findAll();
            all.forEach(System.out::println);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return generalInfo;
    }
}
