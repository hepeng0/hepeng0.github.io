package com.hepeng.example.demo.websocket.example;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * description WebsocketController
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 19:20
 * @since 1.0
 */
@Controller
public class WebsocketController {

    @SubscribeMapping("topic/example/{id}")
    public String sub(@DestinationVariable(value = "id")Long id) {
        return "hello word";
    }

    @SubscribeMapping("topic/example")
    public String sub() {
        return "hello word";
    }
}
