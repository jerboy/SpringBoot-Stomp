package com.jerboy.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@EnableWebSocketMessageBroker
@EnableWebSocket
@Configuration
public class StompConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 注册连接点 path
    registry.addEndpoint("/ws-endpoint");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 设置消息代理为RabbitMQ
    registry.enableStompBrokerRelay("/topic")
        .setVirtualHost("/")
        .setClientLogin("guest")
        .setClientPasscode("guest")
        .setSystemLogin("guest")
        .setSystemPasscode("guest")
        .setSystemHeartbeatSendInterval(5000)
        .setSystemHeartbeatReceiveInterval(5000)
        // 设置用户信息注册
        .setUserDestinationBroadcast("/topic/log-unresolved-user")
        .setUserRegistryBroadcast("/topic/log-user-registry")
    ;

  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {

  }

}
