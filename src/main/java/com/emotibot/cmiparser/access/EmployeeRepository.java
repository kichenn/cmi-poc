package com.emotibot.cmiparser.access;

import com.emotibot.cmiparser.entity.po.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;

/**
 * @Author: zujikang
 * @Date: 2020-04-13 0:44
 */
public interface EmployeeRepository extends ElasticsearchRepository<Employee,Long> {

    //默认的注释
    @Query("{ \"match\": {" +
            "            \"age\": \"?\"" +
            "        }}")

    Page<Employee> findByContent(Long age, Pageable pageable);

}
