package com.hepeng.example.common.config;

import com.hepeng.example.demo.websocket.stomp.HttpSessionIdHandshakeInterceptor;
import com.hepeng.example.demo.websocket.stomp.PresenceChannelInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Websocket配置文件
 * @author hepeng
 * @date 2018/9/18 9:25
 * @since 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 4;
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 8;
    /**
     * 保存秒数
     */
    private static final int INBOUND_KEEP_ALIVE_SECONDS = 60;
    /**
     * 数据大小限制
     */
    private static final int MESSAGE_SIZE_LIMIT = 8192;
    /**
     * 缓存大小限制
     */
    private static final int SEND_BUFFER_SIZE_LIMIT = 8192;
    /**
     * 设置消息发送时间限制毫秒
     */
    private static final int TIME_LIMIT = 10000;

    /**
     * 配置websocket接入点和连接拦截器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //.setAllowedOrigins("*")解决跨域问题
        registry.addEndpoint("/endpointWisely").setAllowedOrigins("*")
                .withSockJS().setInterceptors(httpSessionIdHandshakeInterceptor());
        registry.addEndpoint("/endpointWisely").setAllowedOrigins("*")
                .addInterceptors(httpSessionIdHandshakeInterceptor());

    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        registry.setUserDestinationPrefix("/user");

    }

    /**
     * 消息传输参数配置
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(MESSAGE_SIZE_LIMIT) //设置消息字节数大小
                .setSendBufferSizeLimit(SEND_BUFFER_SIZE_LIMIT)//设置消息缓存大小
                .setSendTimeLimit(TIME_LIMIT); //设置消息发送时间限制毫秒
    }


    /**
     * 输入通道参数设置
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(CORE_POOL_SIZE) //设置消息输入通道的线程池线程数
                .maxPoolSize(MAX_POOL_SIZE) //最大线程数
                .keepAliveSeconds(INBOUND_KEEP_ALIVE_SECONDS); //线程活动时间
        registration.interceptors(presenceChannelInterceptor());
    }

    /**
     * 输出通道参数设置
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(CORE_POOL_SIZE).maxPoolSize(MAX_POOL_SIZE);
        registration.interceptors(presenceChannelInterceptor());
    }

    /**
     * HttpSessionIdHandshakeInterceptor Bean
     * @return HttpSessionIdHandshakeInterceptor
     */
    @Bean
    public HttpSessionIdHandshakeInterceptor httpSessionIdHandshakeInterceptor() {
        return new HttpSessionIdHandshakeInterceptor();
    }

    /**
     * PresenceChannelInterceptor Bean
     * @return PresenceChannelInterceptor
     */
    @Bean
    public PresenceChannelInterceptor presenceChannelInterceptor() {
        return new PresenceChannelInterceptor();
    }

    /**
     * ServerEndpointExporter Bean
     * @return ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


}
