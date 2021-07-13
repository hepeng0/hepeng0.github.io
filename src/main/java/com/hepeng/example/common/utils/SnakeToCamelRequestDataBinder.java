package com.hepeng.example.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * description SnakeToCamelRequestDataBinder
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/4/28 10:54
 * @since 1.0
 */
public class SnakeToCamelRequestDataBinder extends ExtendedServletRequestDataBinder {
    public SnakeToCamelRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);
        Class<?> targetClass = this.getTarget().getClass();
        Field[] fields = targetClass.getDeclaredFields();
        Field[] var5 = fields;
        int var6 = fields.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            JsonProperty jsonPropertyAnnotation = field.getAnnotation(JsonProperty.class);
            if (jsonPropertyAnnotation != null && mpvs.contains(jsonPropertyAnnotation.value()) && !mpvs.contains(field.getName())) {
                mpvs.add(field.getName(), mpvs.getPropertyValue(jsonPropertyAnnotation.value()).getValue());
            }
        }

        List<PropertyValue> covertValues = new ArrayList();
        Iterator var11 = mpvs.getPropertyValueList().iterator();

        while(var11.hasNext()) {
            PropertyValue propertyValue = (PropertyValue)var11.next();
            if (propertyValue.getName().contains("_")) {
                String camelName = SnakeAndCamelUtils.convertSnakeToCamel(propertyValue.getName());
                if (!mpvs.contains(camelName)) {
                    covertValues.add(new PropertyValue(camelName, propertyValue.getValue()));
                }
            }
        }

        mpvs.getPropertyValueList().addAll(covertValues);
    }
}
