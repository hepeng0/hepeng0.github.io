package com.hepeng.example.demo.websocket.example;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * description WebsocketService
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 19:11
 * @since 1.0
 */
@Service
public class WebsocketService {
    /**
     * SimpMessagingTemplate DI
     */
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * SimpUserRegistry
     */
    @Resource
    private SimpUserRegistry simpUserRegistry;

    private final static String DESTINATION_ORIGINAL = "topic/example";

    private String userCode = "example";

    /**
     * 点对点推送
     */
    public void convertAndSendToUser() {
        SimpUser user = simpUserRegistry.getUser(userCode);
        if (Objects.nonNull(user)) {
            simpMessagingTemplate.convertAndSendToUser(user.getName(), DESTINATION_ORIGINAL, "hello word");
        }
    }

    /**
     * 广播推送
     */
    public void convertAndSend() {
        simpMessagingTemplate.convertAndSend(DESTINATION_ORIGINAL, "hello word");
    }
}
