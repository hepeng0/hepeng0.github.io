package com.hepeng.example.common.log;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

/**
 * 日志配置
 */
@Configuration
public class LogConfiguration {
    @Bean
    public LogAdvisor logAdvisor() {
        return new LogAdvisor();
    }

    static class LogAdvisor extends AbstractPointcutAdvisor {
        private static final long serialVersionUID = 2375671248486443144L;
        @Override
        public Advice getAdvice() {
            return new LogAspect();
        }
        @Override
        public Pointcut getPointcut() {
            return new StaticMethodMatcherPointcut(){
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    if(targetClass.isAnnotationPresent(DisableLog.class)){
                        return false;
                    }
                    if(method.isAnnotationPresent(DisableLog.class)){
                        return false;
                    }
                    if(targetClass.isAnnotationPresent(EnableLog.class)){
                        return true;
                    }
                    if(method.isAnnotationPresent(EnableLog.class)){
                        return true;
                    }
                    return targetClass.isAnnotationPresent(RestController.class) || method.isAnnotationPresent(ResponseBody.class);
                }
            };
        }
    }
}