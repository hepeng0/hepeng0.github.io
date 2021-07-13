package com.hepeng.example.common.log;

import com.hepeng.example.common.exception.BaseErrorCode;
import com.hepeng.example.common.exception.BizException;
import com.hepeng.example.common.utils.CommonUtils;
import com.hepeng.example.common.utils.JsonUtlils;
import com.hepeng.example.common.utils.ValidatorUtil;
import com.hepeng.example.domain.common.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志切面
 */
@Slf4j
class LogAspect implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object respData = null;
        long startTime = System.currentTimeMillis();
        String methodInfo = invocation.getThis().getClass().getSimpleName() + "." + invocation.getMethod().getName();
        Object[] objs = invocation.getArguments();
        List<Object> argsList = new ArrayList<>();
        for(Object obj : objs){
            if(obj instanceof ServletRequest || obj instanceof ServletResponse || obj instanceof MultipartFile){
                continue;
            }
            argsList.add(obj);
        }
        log.info("{}()被调用，入参为{}", methodInfo, JsonUtlils.writeValueAsString(argsList));
        // 表单验证
        for(Object obj : argsList){
            if(null!=obj && ((obj.getClass().isAnnotationPresent(EnableFormValid.class) || invocation.getMethod().isAnnotationPresent(EnableFormValid.class)) || invocation.getThis().getClass().isAnnotationPresent(EnableFormValid.class))){
                String validateResult = ValidatorUtil.validate(obj);
                log.info("{}()的表单-->{}", methodInfo, StringUtils.isBlank(validateResult)?"验证通过":"验证未通过");
                if (StringUtils.isNotBlank(validateResult)) {
                    respData = RestResult.fail(BaseErrorCode.PARAMS_CHK_ERROR.getErrorCode(), validateResult);
                }
            }
        }
        if(null == respData){
            try{
                respData = invocation.proceed();
            }catch(Throwable cause){
                // 异常处理
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                // if(null == attributes){
                //     return invocation.proceed();
                // }
                log.info("Exception Occured URL=" + attributes.getRequest().getRequestURL() + "，堆栈轨迹如下", cause);
                String code;
                String msg = cause.getMessage();
                if(cause instanceof BizException){
                    code = ((BizException)cause).getErrorCode();
                }else{
                    code = BaseErrorCode.INTERNAL_EXCEPTION.getErrorCode();
                    msg = CommonUtils.extractStackTraceCausedBy(cause);
                }
                respData = RestResult.fail(code, msg);
            }
        }
        // 打印出参并返回
        long endTime = System.currentTimeMillis();
        String returnInfo;
        if(null == respData){
            returnInfo = "<null>";
        }else if(respData instanceof InputStream) {
            returnInfo = "<java.io.InputStream>";
        }else if(respData instanceof ServletRequest) {
            returnInfo = "<javax.servlet.ServletRequest>";
        }else if(respData instanceof ServletResponse) {
            returnInfo = "<javax.servlet.ServletResponse>";
        }else if(respData.getClass().isAssignableFrom(ResponseEntity.class)) {
            returnInfo = "<org.springframework.http.ResponseEntity>";
        }else{
            returnInfo = JsonUtlils.writeValueAsString(respData);
        }
        log.info("{}()被调用，出参为{}，Duration[{}]ms", methodInfo, returnInfo, endTime-startTime);
        log.info("---------------------------------------------------------------------------------------------");
        return respData;
    }
}