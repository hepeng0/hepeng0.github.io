package com.hepeng.example.common.validation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class EnumValidator implements ConstraintValidator<EnumValidate, Object> {
    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(EnumValidator.class);

    /**
     * 枚举校验注解
     */
    private EnumValidate annotation;

    @Override
    public void initialize(EnumValidate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) {
            return annotation.ignoreEmpty();
        }
        if (StringUtils.isBlank(annotation.enumMethod())) {
            return Boolean.TRUE;
        }
        Method method;
        try {
            method = annotation.enumClass().getMethod(annotation.enumMethod());
        } catch (Exception e) {
            return true;
        }
        return Arrays.stream(annotation.enumClass().getEnumConstants())
                .anyMatch(o -> {
                    try {
                        return value.equals(method.invoke(o));
                    } catch (Exception e) {
                        log.error("enum validate exception: ", e);
                        return false;
                    }
                });
    }
}
