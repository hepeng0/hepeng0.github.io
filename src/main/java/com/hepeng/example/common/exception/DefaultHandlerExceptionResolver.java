package com.hepeng.example.common.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * description DefaultHandlerExceptionResolver
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 16:43
 * @since 1.0
 */
public class DefaultHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class);
    private String prjCode;

    public DefaultHandlerExceptionResolver(String prjCode) {
        this.prjCode = prjCode;
    }

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error(String.format("An exception occurs when call handler: %s", handler == null ? "" : handler.toString()), ex);
        ModelAndView modelAndView = new ModelAndView();
        MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
        Map<String, Object> attributesMap = new HashMap();
        if (ex instanceof BizException) {
            BizException _ex = (BizException)ex;
            attributesMap.put("error", "biz Error");
            attributesMap.put("message", _ex.getMessage());
            attributesMap.put("error_code", this.prjCode + _ex.getErrorCode());
            response.setStatus(_ex.getHttpStatus());
        } else {
            StringBuffer sb;
            Iterator var10;
            if (ex instanceof ConstraintViolationException) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                ConstraintViolationException constraintViolationException = (ConstraintViolationException)ex;
                sb = new StringBuffer();
                var10 = constraintViolationException.getConstraintViolations().iterator();

                while(var10.hasNext()) {
                    ConstraintViolation c = (ConstraintViolation)var10.next();
                    sb.append(c.getMessage()).append("; ");
                }

                attributesMap.put("message", BaseErrorCode.PARAMS_CHK_ERROR.getMessage() + " : " + sb.toString());
                attributesMap.put("error_code", this.prjCode + BaseErrorCode.PARAMS_CHK_ERROR.getErrorCode());
            } else {
                ObjectError objectError;
                if (ex instanceof MethodArgumentNotValidException) {
                    List<ObjectError> errorList = ((MethodArgumentNotValidException)ex).getBindingResult().getAllErrors();
                    sb = new StringBuffer();
                    var10 = errorList.iterator();

                    while(var10.hasNext()) {
                        objectError = (ObjectError)var10.next();
                        sb.append(objectError.getDefaultMessage()).append("; ");
                    }

                    attributesMap.put("message", BaseErrorCode.PARAMS_CHK_ERROR.getMessage() + " : " + sb.toString());
                    attributesMap.put("error_code", this.prjCode + BaseErrorCode.PARAMS_CHK_ERROR.getErrorCode());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                } else if (ex instanceof NoHandlerFoundException) {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    attributesMap.put("message", BaseErrorCode.REQUEST_PATH_ERROR.getMessage());
                    attributesMap.put("error_code", this.prjCode + BaseErrorCode.REQUEST_PATH_ERROR.getErrorCode());
                } else if (ex instanceof BindException) {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    BindException bindException = (BindException)ex;
                    sb = new StringBuffer();
                    var10 = bindException.getAllErrors().iterator();

                    while(var10.hasNext()) {
                        objectError = (ObjectError)var10.next();
                        sb.append(objectError.getDefaultMessage()).append("; ");
                    }

                    attributesMap.put("message", BaseErrorCode.PARAMS_CHK_ERROR.getMessage() + " : " + sb.toString());
                    attributesMap.put("error_code", this.prjCode + BaseErrorCode.PARAMS_CHK_ERROR.getErrorCode());
                } else {
                    attributesMap.put("error", ex.getMessage());
                    attributesMap.put("message", BaseErrorCode.INTERNAL_ERROR.getMessage());
                    attributesMap.put("error_code", this.prjCode + BaseErrorCode.INTERNAL_ERROR.getErrorCode());
                    attributesMap.put("exception", ExceptionUtils.getStackTrace(ex));
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }
        }

        mappingJackson2JsonView.setAttributesMap(attributesMap);
        modelAndView.setView(mappingJackson2JsonView);
        return modelAndView;
    }

    public int getOrder() {
        return -2147483648;
    }
}