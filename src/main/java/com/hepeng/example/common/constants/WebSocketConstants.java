package com.hepeng.example.common.constants;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * description ConfigConstants
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 18:52
 * @since 1.0
 */
public interface WebSocketConstants {
    /**
     * websocket 配置相关
     */
    String REMOTE_ADDRESS = "remoteAddress";
    String ID_KEY = "idKey";
    /**
     * 缓存当前websocket连接的信息，包括终端唯一标识和对应的设备ID
     */
    ConcurrentMap<String, ArrayList<String>> CURRENT_ALIVE_SOCKET_MAP = new ConcurrentHashMap<>();
}
