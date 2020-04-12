package com.emotibot.cmiparser.controller;

import com.emotibot.cmiparser.access.EmployeeRepository;
import com.emotibot.cmiparser.entity.po.Employee;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zujikang
 * @Date: 2020-04-13 0:48
 */
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    private Pageable pageable = PageRequest.of(0,10);

    @GetMapping("/article/add")
    public String add() {

        Employee employee = new Employee();
        employee.setAbout("haha");
        return employeeRepository.save(employee).toString();

    }

    @GetMapping("/employee/get")

    public Object get(){
       /* Iterable<Employee> list = employeeRepository.findAll();
        list.forEach(System.out::println);

        Page<Employee> haha = employeeRepository.findByContent(32L, pageable);
        List<Employee> collect = haha.get().collect(Collectors.toList());
        System.out.println(collect);*/
       //===========================

        /**
         * must 多条件 &（并且）
         * mustNot 多条件 != (非)
         * should 多条件 || (或)
         */
//        QueryBuilder query = QueryBuilders.boolQuery()
//                .must(QueryBuilders.termQuery("userId","2019040499"))  //精确查询
//                .must(QueryBuilders.wildcardQuery("userName","*华腾"))    //模糊查询
//                .must(QueryBuilders.rangeQuery("createDate"));
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("first_name", "Jane");

//        List<String> properties = new ArrayList<>();
//        Sort sort = Sort.by(Sort.Direction.DESC,"createDate");//排序

        //多条件排序
//    Sort.Order order1 = new Sort.Order(Sort.Direction.DESC,"userId");
//    Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"userId");
//    Sort sortList = Sort.by(order1,order2);

        //Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Employee> search = employeeRepository.search(matchQueryBuilder, pageable);
        List<Employee> collect = search.get().collect(Collectors.toList());
        System.out.println(collect);


        return null;
    }



}
