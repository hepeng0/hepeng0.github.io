package com.hepeng.example.demo.websocket.original;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: WebSocketServer
 *
 * @author hepeng
 * @date 2018/10/10 14:00
 * @since 1.0
 */
@ServerEndpoint(value = "/websocket/{id}")
@Component
@Slf4j
public class WebSocketServer {

	/**
	 * session
	 */
	private static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();

	/**
	 * 打开连接时触发
	 * @param id
	 *            id
	 * @param session
	 *            session
	 */
	@OnOpen
	public void onOpen(@PathParam("id") String id, Session session) {
	    String info = id + "连接websocket";
	    log.info(info);
		CLIENTS.put(id, session);
	}

	/**
	 * 收到客户端消息时触发
	 * @param id
	 *            id
	 * @param message
	 *            消息
	 * @return string
	 */
	@OnMessage
	public String onMessage(@PathParam("id") String id, String message) {
		return message;
	}

	/**
	 * 异常时触发
	 * @param id
	 *            id
	 * @param throwable
	 *            异常
	 */
	@OnError
	public void onError(@PathParam("id") String id, Throwable throwable) {
	    String errorMessage = "websocket" + id + "------" + throwable.getMessage();
		log.error(errorMessage);
		CLIENTS.remove(id);
	}

	/**
	 * 关闭连接时触发
	 * @param id
	 *            id
	 */
	@OnClose
	public void onClose(@PathParam("id") String id) {
	    String info = "websocket " + id + "------断开连接";
	    log.info(info);
		CLIENTS.remove(id);
	}

	/**
	 * 将数据传回客户端 异步的方式
	 * @param type
	 *            类型，list,number,queue
	 */
	public static void broadcast(String type) {
		for (Map.Entry<String, Session> client : CLIENTS.entrySet()) {
			if (type.equals(client.getKey().split(":")[0])) {
				Session session = client.getValue();
				synchronized (session) {
					session.getAsyncRemote().sendText(type);
				}
			}
		}
	}
}
