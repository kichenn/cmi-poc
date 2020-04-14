package com.emotibot.cmiparser.service;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.cmiparser.access.HotelRepository;
import com.emotibot.cmiparser.entity.bo.PriceParserBo;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.entity.po.Kw;
import com.emotibot.cmiparser.util.ParserUtils;
import com.emotibot.cmiparser.util.PreHandlingUnit;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zujikang
 * @Date: 2020/4/13 14:03
 */
@Slf4j
@Service
public class HotelViewService {

    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private HotelRepository hotelRepository;
    private Pageable pageable = PageRequest.of(0, 10);

    @PostMapping("/hotelView")
    public List<HotelEntity> hotelView(String userId) {
        try {

            /*
            模拟数据
             */
//            ArrayList<String> hotelStars = new ArrayList<>();
//            hotelStars.add("五星级");
//            hotelStars.add("一星级");
//            ArrayList<String> objects = new ArrayList<>();
//            objects.add("3000");
//            PriceParserBo priceParserBo1 = PriceParserBo.builder().amount(300000).range("L").entities(objects).build();
//            UserCache userCache = UserCache.builder().ccity("香港").checkinTime(new Date()).checkoutTime(new Date())
////                    .hotelName("香港半岛酒店")
//                    .hotelStars(hotelStars)
//                    .roomType("豪华间")
//                    .specials("优惠")
//                    .priceParserBo(priceParserBo1)
////                    .district("尖沙咀")
//                    .build();

            signUp(userId);
            UserCache userCache = innerCache.get(userId);

            List<HotelEntity> collect = null;

            /**
             * 直接指定酒店名称
             */
            if (userCache.getHotelName() != null) {
                Pageable pageable = PageRequest.of(0, 1);
                MatchPhraseQueryBuilder queryEntity = QueryBuilders.matchPhraseQuery("entity", userCache.getHotelName());
                Page<HotelEntity> search = hotelRepository.search(queryEntity, pageable);
                HotelEntity hotelEntity = search.get().findAny().get();
                if (hotelEntity != null) {
                    collect.add(hotelEntity);
                    innerCache.get(userId).setHotels(collect);
                    return collect;
                }
            }

            /*
            优先查出“**豪华间”等实体，根据belongto查酒店              ps:es不方便关联查询
             */
            ArrayList<HotelEntity> hotelEntities1 = new ArrayList<>();
            ArrayList<HotelEntity> hotelEntities2 = new ArrayList<>();
            if (!StringUtils.isEmpty(userCache.getRoomType())) {
                WildcardQueryBuilder entity = QueryBuilders.wildcardQuery("entity", "*" + userCache.getRoomType());
                Iterable<HotelEntity> search = hotelRepository.search(entity);
                search.forEach(hotelEntities1::add);
            }

            if (userCache.getPriceParserBo() != null) {
                PriceParserBo priceParserBo = userCache.getPriceParserBo();
                List<String> entities = priceParserBo.getEntities();
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                if (entities.size() >= 2) {
                    Long beginPrice = Long.parseLong(PreHandlingUnit.numberTranslator(entities.get(0)));
                    Long endPrice = Long.parseLong(PreHandlingUnit.numberTranslator(entities.get(1)));
                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                    .must(QueryBuilders.rangeQuery("r_float.value").gte(beginPrice).lte(endPrice)), ScoreMode.Max));

                } else if (entities.size() == 1) {
                    String range = priceParserBo.getRange();
                    Long onlyPrice = Long.parseLong(PreHandlingUnit.numberTranslator(entities.get(0)));
                    if ("L".equals(range)) {
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").lte(onlyPrice)), ScoreMode.Max));
                    }
                    if ("R".equals(range)) {
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").gte(onlyPrice)), ScoreMode.Max));
                    }
                    if ("LR".equals(range) || StringUtils.isEmpty(range)) {
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").gte(onlyPrice - 200L).lte(onlyPrice + 200L)), ScoreMode.Max));
                    }
                }
                Iterable<HotelEntity> search = hotelRepository.search(boolQueryBuilder);
                search.forEach(hotelEntities2::add);
            }

            if(!CollectionUtils.isEmpty(hotelEntities1) && !CollectionUtils.isEmpty(hotelEntities2)) {
                hotelEntities1.retainAll(hotelEntities2);
            }else{
                if(!CollectionUtils.isEmpty(hotelEntities2)) {
                    hotelEntities1 = hotelEntities2;
                }
            }


//            hotelEntities1.forEach(System.out::println);


            /*
            查酒店
             */
            ArrayList<HotelEntity> results = new ArrayList<>();

            for (HotelEntity hotelEntity : hotelEntities1) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                Kw belongs_to = hotelEntity.getR_kw().stream().filter(e -> e.getAttr().equals("belongs to")).findFirst().get();
                String entityName = belongs_to.getValue();

                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("entity", entityName));

                if (!StringUtils.isEmpty(userCache.getSpecials())) {
                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "无忧行特惠"))
                                    .must(QueryBuilders.termQuery("r_float.value_show", "1")), ScoreMode.Max));
                }

                if (!StringUtils.isEmpty(userCache.getDistrict())) {
                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_text", QueryBuilders.boolQuery().must(
                                    QueryBuilders.matchPhraseQuery("r_text.attr", "区商圈"))
                                    .must(QueryBuilders.matchPhraseQuery("r_text.value", userCache.getDistrict())), ScoreMode.Max));
                }

                if (!CollectionUtils.isEmpty(userCache.getHotelStars())) {
                    List<Integer> stars = ParserUtils.hotelStarParse(userCache.getHotelStars());
                    Collections.sort(stars);
                    int begin = stars.get(0);
                    int end = stars.get(stars.size() - 1);

                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "星级"))
                                    .must(QueryBuilders.rangeQuery("r_float.value_show").gte(begin).lte(end)), ScoreMode.Max));
                }

                //取出套间面积和价格
                Iterable<HotelEntity> search = hotelRepository.search(boolQueryBuilder);
                ArrayList<HotelEntity> h = new ArrayList<>();
                if (search.iterator().hasNext()) {
                    search.forEach(h::add);
                    HotelEntity entity = h.get(0);
                    hotelEntity.getR_text().stream().forEach(em -> {
                        if (em.getAttr().equals("面积")) entity.setRoomArea(em.getValue());
                    });
                    hotelEntity.getR_float().stream().forEach(en -> {
                        if (en.getAttr().equals("单晚价格")) entity.setCurrentPrice(en.getValue());
                    });

                    results.add(entity);
                }

            }

//            results.forEach(System.out::println);

            //不会嵌套排序表达式，暂时在外面排序再取10个
            Collections.sort(results);
            collect = results;
            if (results.size() > 10) {
                collect = results.stream().limit(10).collect(Collectors.toList());
            }
//            collect.forEach(System.out::println);
            innerCache.get(userId).setHotels(collect);
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void signUp(String userId) {
        try {
            innerCache.get(userId);
        } catch (Exception e) {
            if (e instanceof CacheLoader.InvalidCacheLoadException) {
                innerCache.put(userId, UserCache.builder().build());
            }
        }
    }
}
