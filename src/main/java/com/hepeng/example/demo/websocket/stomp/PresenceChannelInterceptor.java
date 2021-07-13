package com.hepeng.example.demo.websocket.stomp;

import com.hepeng.example.common.constants.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.LinkedList;
import java.util.Map;


/**
 * websocket拦截器
 */
@Slf4j
public class PresenceChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                Object userString = ((Map) raw).get("user_code");
                String userCode = "";
                if (userString instanceof LinkedList) {
                    userCode = ((LinkedList) userString).get(0).toString();
                }
                accessor.setUser(new WebsocketUser(userCode));
            }
        } else {
            log.info("presendMessage: {}", message);
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        Map nativeHeadersMap = (Map) message.getHeaders().get("nativeHeaders");
        String logStr = null;

        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        // ignore non-STOMP messages like heartbeat messages
        if (sha.getCommand() == null) {
            return;
        }
        String remoteAddress = "unknown";
        if (sha.getSessionAttributes() != null && sha.getSessionAttributes().get(WebSocketConstants.REMOTE_ADDRESS) != null) {
            remoteAddress = sha.getSessionAttributes().get(WebSocketConstants.REMOTE_ADDRESS).toString();
        }

        if (sha.getSessionAttributes().get(WebSocketConstants.ID_KEY) == null && nativeHeadersMap != null) {

            LinkedList idList = (LinkedList) nativeHeadersMap.get("id");
            if (idList != null) {
                logStr = "终端唯一标识符id:---" + idList.get(0).toString();
                log.info(logStr);
                sha.getSessionAttributes().put(WebSocketConstants.ID_KEY, idList.get(0).toString());

            }

        }
        //判断客户端的连接状态
        switch (sha.getCommand()) {
            case CONNECT:
                logStr = remoteAddress + ":-PresenceChannelInterceptor-------连接成功";
                log.info(logStr);
                break;
            case CONNECTED:
                break;
            case DISCONNECT:
                logStr = remoteAddress + ":PresenceChannelInterceptor-------断开连接";
                log.info(logStr);
                if (sha.getSessionAttributes().get(WebSocketConstants.ID_KEY) != null) {
                    String id = sha.getSessionAttributes().get(WebSocketConstants.ID_KEY).toString();
                    logStr = remoteAddress + ":PresenceChannelInterceptor-------断开连接的id:" + id;
                    log.info(logStr);
                    WebSocketConstants.CURRENT_ALIVE_SOCKET_MAP.remove(id);
                }
                break;
            default:
                break;
        }
    }

}

