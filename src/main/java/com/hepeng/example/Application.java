package com.hepeng.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * description Application
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/4/28 10:37
 * @since 1.0
 */
@EnableCaching
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // 针对404错误，抛出异常
        DispatcherServlet dispatcherServlet = (DispatcherServlet) ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }

}
