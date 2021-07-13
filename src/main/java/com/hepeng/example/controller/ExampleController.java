package com.hepeng.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * description ExampleController
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/11 18:03
 * @since 1.0
 */
@Controller
@RestController
public class ExampleController {

    @RequestMapping(value = "**")
    public Object restApi(@RequestBody(required = false) String requestBody, HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();

        System.out.println(method);
        System.out.println(uri);
        return "";
    }
}
