package com.hepeng.example.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置类
 *
 * @author daisike [dai.sike@unisinsight.com]
 * @date 2018/9/7 9:38
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties()
public class RedisAutoConfiguration extends CachingConfigurerSupport {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAutoConfiguration.class);


    /**
     * redisTemplate配置
     * @param factory redis连接模式
     * @return RedisTemplate<String                               ,                                                               Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key 序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);

        // value序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
                Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }


    /**
     * redis springCache缓存manager
     * @param factory redis连接模式
     * @return 缓存管理
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * redis数据操作异常处理 这里的处理：在日志中打印出错误信息，但是放行
     * 保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
       return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
                redisErrorException(e, o);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
                redisErrorException(e, o);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
                redisErrorException(e, o);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                redisErrorException(e, null);
            }
        };
    }

    /**
     * 异常打印
     * @param exception
     * @param key
     */
    protected void redisErrorException(Exception exception, Object key) {
        LOGGER.error("redis异常：key=[{}], exception={}", key, exception.getMessage());
    }


}
