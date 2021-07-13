package com.hepeng.example.demo.websocket.stomp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.security.Principal;


/**
 * description WebsocketUser
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 19:00
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class WebsocketUser implements Principal {
    private String userCode;
    private String group;

    public WebsocketUser(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getName() {
        return userCode;
    }
}
