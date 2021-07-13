package com.hepeng.example.demo.websocket.stomp;

import com.hepeng.example.common.constants.WebSocketConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * websocket握手（handshake）接口
 * Created by earl on 2017/4/17.
 */
public class HttpSessionIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor  {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionIdHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        //解决The extension [x-webkit-deflate-frame] is not supported问题
        String logStr = "beforeHandshake:getRemoteAddress---" + request.getRemoteAddress();
        LOGGER.info(logStr);
        attributes.put(WebSocketConstants.REMOTE_ADDRESS, request.getRemoteAddress());
        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}

