package com.emotibot.cmiparser.service;

import com.emotibot.cmiparser.access.HotelRepository;
import com.emotibot.cmiparser.entity.dto.UserCache;
import com.emotibot.cmiparser.entity.dto.UserQuery;
import com.emotibot.cmiparser.entity.po.HotelEntity;
import com.emotibot.cmiparser.entity.po.Kw;
import com.emotibot.cmiparser.util.ParserUtils;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotelViewService {

    @Autowired
    private LoadingCache<String, UserCache> innerCache;
    @Autowired
    private HotelRepository hotelRepository;

    @PostMapping("/hotelView")
    public List<HotelEntity> hotelView(UserQuery userQuery, String userId) {
        try {

            List<HotelEntity> collect = new ArrayList<>();

            /**
             * 直接指定酒店名称
             */
            if (!StringUtils.isEmpty(userQuery.getHotelName())) {
                Pageable pageable = PageRequest.of(0, 1);
                MatchPhraseQueryBuilder queryEntity = QueryBuilders.matchPhraseQuery("entity", userQuery.getHotelName());
                Page<HotelEntity> search = hotelRepository.search(queryEntity, pageable);
                HotelEntity hotelEntity = search.get().findAny().get();
                if (hotelEntity != null) {
                    List<Kw> r_kw = hotelEntity.getR_kw();
                    ArrayList<String> roomTypeHotel = new ArrayList<>();
                    r_kw.stream().forEach(e -> {
                        if (e.getAttr().equals("包含")) roomTypeHotel.add(e.getValue());
                    });

                    if (!CollectionUtils.isEmpty(roomTypeHotel)) {
                        for (String s : roomTypeHotel) {
                            if (s.contains(userQuery.getRoomType())) {
                                Optional<String> first = null;
                                try {
                                    first = roomTypeHotel.stream().filter(e -> e.contains(userQuery.getRoomType())).findFirst();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (first.isPresent()) {
                                    String sonHotelName = first.get();
                                    MatchPhraseQueryBuilder queryEntity2 = QueryBuilders.matchPhraseQuery("entity", sonHotelName);
                                    Page<HotelEntity> search2 = hotelRepository.search(queryEntity2, pageable);
                                    HotelEntity hotelEntity2 = search2.get().findAny().get();

                                    hotelEntity2.getR_text().stream().forEach(em -> {
                                        if (em.getAttr().equals("面积")) hotelEntity.setRoomArea(em.getValue());
                                    });
                                    hotelEntity2.getR_float().stream().forEach(en -> {
                                        if (en.getAttr().equals("单晚价格")) hotelEntity.setCurrentPrice(en.getValue());
                                    });
                                }
                                break;
                            }
                        }

                    }

                    collect.add(hotelEntity);
                    collect.forEach(e -> {
                        e.setCheckinTime(userQuery.getCheckinTime());
                        e.setCheckoutTime(userQuery.getCheckoutTime());
                        e.setRoomType(userQuery.getRoomType());
                    });
                    innerCache.get(userId).setHotels(collect);
                    return collect;
                }
            }

            /**
            优先查出“**豪华间”等实体，根据belongto查酒店              ps:es不方便关联查询
             */
            ArrayList<HotelEntity> hotelEntities1 = new ArrayList<>();
            ArrayList<HotelEntity> hotelEntities2 = new ArrayList<>();
            if (!StringUtils.isEmpty(userQuery.getRoomType())) {
                WildcardQueryBuilder entity = QueryBuilders.wildcardQuery("entity", "*" + userQuery.getRoomType());
                Iterable<HotelEntity> search = hotelRepository.search(entity);
                search.forEach(hotelEntities1::add);
            }

            if (!StringUtils.isEmpty(userQuery.getPrice())) {
                String price = userQuery.getPrice();
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                if (price.contains("-")) {
                    String[] split = price.split("-");
                    Long beginPrice = Long.parseLong(split[0]);
                    Long endPrice = Long.parseLong(split[1]);

                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                    .must(QueryBuilders.rangeQuery("r_float.value").gte(beginPrice).lte(endPrice)), ScoreMode.Max));

                } else {

                    if (price.contains("LR")) {
                        Long onlyPrice = Long.parseLong(price.split("LR")[0]);
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").gte(onlyPrice - 200L).lte(onlyPrice + 200L)), ScoreMode.Max));

                    } else if (price.contains("L")) {
                        Long onlyPrice = Long.parseLong(price.split("L")[0]);
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").lte(onlyPrice)), ScoreMode.Max));
                    } else if (price.contains("R")) {
                        Long onlyPrice = Long.parseLong(price.split("R")[0]);
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").gte(onlyPrice)), ScoreMode.Max));
                    } else {
                        Long onlyPrice = Long.parseLong(price);
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(
                                "r_float", QueryBuilders.boolQuery().must(
                                        QueryBuilders.termQuery("r_float.attr", "单晚价格"))
                                        .must(QueryBuilders.rangeQuery("r_float.value").gte(onlyPrice - 200L).lte(onlyPrice + 200L)), ScoreMode.Max));
                    }
                }
                Iterable<HotelEntity> search = hotelRepository.search(boolQueryBuilder);
                search.forEach(hotelEntities2::add);

                //如果有价格，取交集
                hotelEntities1.retainAll(hotelEntities2);
            }



            /**
            综合其他条件查酒店
             */
            ArrayList<HotelEntity> results = new ArrayList<>();

            for (HotelEntity hotelEntity : hotelEntities1) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                Kw belongs_to = hotelEntity.getR_kw().stream().filter(e -> e.getAttr().equals("belongs to")).findFirst().get();
                String entityName = belongs_to.getValue();

                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("entity", entityName));

                if (!StringUtils.isEmpty(userQuery.getSpecials())) {
                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "无忧行特惠"))
                                    .must(QueryBuilders.termQuery("r_float.value_show", "1")), ScoreMode.Max));
                }

                if (!StringUtils.isEmpty(userQuery.getDistrict())) {
                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_text", QueryBuilders.boolQuery().must(
                                    QueryBuilders.matchPhraseQuery("r_text.attr", "区商圈"))
                                    .must(QueryBuilders.matchPhraseQuery("r_text.value", userQuery.getDistrict())), ScoreMode.Max));
                }

                if (!StringUtils.isEmpty(userQuery.getHotelStars())) {
                    List<String> stars = ParserUtils.hotelStarParse(userQuery.getHotelStars());

                    boolQueryBuilder.must(QueryBuilders.nestedQuery(
                            "r_float", QueryBuilders.boolQuery().must(
                                    QueryBuilders.termQuery("r_float.attr", "星级"))
                                    .must(QueryBuilders.termsQuery("r_float.value_show",stars)),ScoreMode.Max));
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


            //不会嵌套排序表达式，暂时在外面排序再取10个
            Collections.sort(results);
            collect = results.size() > 10 ?  results.stream().limit(10).collect(Collectors.toList()) : results;
            collect.forEach(e -> {
                e.setCheckinTime(userQuery.getCheckinTime());
                e.setCheckoutTime(userQuery.getCheckoutTime());
                e.setRoomType(userQuery.getRoomType());
            });
            innerCache.get(userId).setHotels(collect);
            return collect;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


}
