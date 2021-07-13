package com.hepeng.example.demo.websocket.stomp;


import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * 扩展功能使用
 */
@Component
public class STOMPConnectEventListener implements ApplicationListener<SessionSubscribeEvent> {

    /**
     * 后续扩展使用
     * @param sessionSubscribeEvent
     */
    @Override
    public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        //后续扩展使用
     }
}
