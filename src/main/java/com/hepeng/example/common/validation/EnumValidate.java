package com.hepeng.example.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {EnumValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface EnumValidate {
    /**
     * 校验报错信息
     */
    String message() default "枚举值校验异常";

    /**
     * 校验分组
     */
    Class<?>[] groups() default {};

    /**
     * 附件 用于扩展
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 要校验的枚举方法类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 调用的枚举类中的方法
     */
    String enumMethod() default "name";

    /**
     * 是否忽略空值
     */
    boolean ignoreEmpty() default true;

    /**
     * Defines several {@link EnumValidate} annotations on the same element.
     *
     * @see EnumValidate
     */
    @Documented
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @interface List {
        EnumValidate[] value();
    }
}
