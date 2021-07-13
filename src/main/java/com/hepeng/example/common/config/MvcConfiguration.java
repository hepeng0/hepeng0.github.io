package com.hepeng.example.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.hepeng.example.common.exception.DefaultHandlerExceptionResolver;
import com.hepeng.example.common.utils.SnakeToCamelModelAttributeMethodProcessor;
import com.hepeng.example.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * description MvnConfiguration
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/4/28 10:40
 * @since 1.0
 */
@Configuration
@Slf4j
public class MvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * GET请求下划线转驼峰策略
     * @return
     */
    @Bean
    SnakeToCamelModelAttributeMethodProcessor snakeToCamelModelAttributeMethodProcessor() {
        return new SnakeToCamelModelAttributeMethodProcessor(true);
    }

    /**
     * 异常处理器
     * @param resolvers 全局异常处理器
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new DefaultHandlerExceptionResolver("0000"));
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(snakeToCamelModelAttributeMethodProcessor());
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .failOnUnknownProperties(false)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Bean
    public UserUtils.UserInterceptor userInterceptor() {
        return new UserUtils.UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor()).addPathPatterns("/**");
    }

}
