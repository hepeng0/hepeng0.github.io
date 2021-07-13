package com.hepeng.example.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.Objects;

/**
 * description JsonUtlils
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/4/28 14:37
 * @since 1.0
 */
public class JsonUtlils {

    private static ThreadLocal<ObjectMapper> objectMapperThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<ObjectMapper> snakeCaseObjectMapperThreadLocal = new ThreadLocal<>();


    private JsonUtlils() {
        throw new IllegalStateException("Utility class");
    }

    public static JsonNode readTree(String json) {
        return readTree(getSnakeCaseObjectMapper(), json);
    }

    public static JsonNode readTree(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public  static <T> T readValue(ObjectMapper objectMapper, String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  static <T> T readValue(String json, JavaType javaType) {
        return readValue(getSnakeCaseObjectMapper(), json, javaType);
    }

    public  static <T> T readValue(ObjectMapper objectMapper, String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  static <T> T readValue(String json, TypeReference<T> reference) {
        return readValue(getSnakeCaseObjectMapper(), json, reference);
    }

    public  static <T> T readValue(ObjectMapper objectMapper, String json, TypeReference<T> reference) {
        try {
            return objectMapper.readValue(json, reference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String writeValueAsString(T value) {
        return writeValueAsString(getSnakeCaseObjectMapper(), value);
    }
    /**
     * 对象转json字符串
     * @param objectMapper
     * @param value
     * @return
     */
    public static <T> String writeValueAsString(ObjectMapper objectMapper, T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = objectMapperThreadLocal.get();
        if (Objects.isNull(objectMapper)) {
            objectMapper = new ObjectMapper();
            objectMapperThreadLocal.set(objectMapper);
        }
        return objectMapper;
    }

    public static ObjectMapper getSnakeCaseObjectMapper() {
        ObjectMapper objectMapper = snakeCaseObjectMapperThreadLocal.get();
        if (objectMapper == null) {
            Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
            jacksonObjectMapperBuilder.failOnUnknownProperties(false);
            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper = jacksonObjectMapperBuilder.build();
            snakeCaseObjectMapperThreadLocal.set(objectMapper);
        }
        return objectMapper;
    }
}
