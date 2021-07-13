package com.hepeng.example.common.utils;

import com.hepeng.example.domain.bo.UserBO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * description UserUtils
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/13 10:55
 * @since 1.0
 */
public class UserUtils {
    private static ThreadLocal<UserBO> user = new ThreadLocal<>();

    public static void setUser(UserBO userBO) {
        user.set(userBO);
    }

    public static UserBO getUser() {
        return user.get();
    }

    public static class UserInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            Cookie[] cookies = request.getCookies();
            UserBO userBO = new UserBO();
            if (Objects.nonNull(cookies)) {
                for(Cookie cookie: cookies) {
                    if(Objects.equals(cookie.getName(), "usercode")) {
                        userBO.setUsercode(cookie.getValue());
                    }
                }
            }
            setUser(userBO);
            return true;
        }
    }
}
