package com.emotibot.cmiparser.config;

import com.emotibot.cmiparser.entity.dto.UserCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MyCacheConfiguration {

    //代表key在最后一次访问1h后被清除
    private static final int EXPIRE_SECONDS = 3600;

    /**
     * 定义token缓存, 默认最大数量为3000
     */
    @Bean
    public LoadingCache<String, UserCache> myCacheStorage() {
        try {
            return CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(EXPIRE_SECONDS, TimeUnit.SECONDS)
                            .build(new CacheLoader<String, UserCache>() {
                                @Override
                                public UserCache load(String name) throws Exception {
                                    //在这里可以初始化加载数据的缓存信息，读取数据库中信息或者是加载文件中的某些数据信息
                                    return null;
                                }
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}