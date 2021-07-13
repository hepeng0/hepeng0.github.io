package com.hepeng.example.common.utils;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import javax.servlet.ServletRequest;

/**
 * description SnakeToCamelModelAttributeMethodProcessor
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/4/28 10:52
 * @since 1.0
 */
public class SnakeToCamelModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {
    public SnakeToCamelModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        SnakeToCamelRequestDataBinder camelBinder = new SnakeToCamelRequestDataBinder(binder.getTarget(), binder.getObjectName());
        camelBinder.bind(request.getNativeRequest(ServletRequest.class));
    }

}
