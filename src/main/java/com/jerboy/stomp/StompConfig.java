package com.jerboy.stomp;

import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@EnableWebSocketMessageBroker
@EnableWebSocket
@Configuration
@Slf4j
public class StompConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 注册连接点 path
    registry.addEndpoint("/ws-endpoint")
        .setHandshakeHandler(new HandshakeHandler() {
          @Override
          public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response,
              WebSocketHandler wsHandler, Map<String, Object> attributes)
              throws HandshakeFailureException {
            // 如果未登录用户在这里拦截
            return true;
          }
        })
        .setHandshakeHandler(new DefaultHandshakeHandler(){
          @Override
          protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
              Map<String, Object> attributes) {
//            return super.determineUser(request, wsHandler, attributes);
            return new Principal() {
              @Override
              public String getName() {
                // TODO 根据session 返回连接的用户米看那个成
                return "default-user";
              }
            };
          }
        })
        .setAllowedOrigins("*");
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
        .setRelayHost("127.0.0.1")// RabbitMQ 地址
        .setRelayPort(61613)    // STOMP 端口
        // 设置用户信息注册
        .setUserDestinationBroadcast("/topic/log-unresolved-user")
        .setUserRegistryBroadcast("/topic/log-user-registry");
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
      @Override
      public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
          @Override
          public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            super.afterConnectionEstablished(session);
            log.info("连接建立 " + session);
            // WS 连接建立前
          }

          @Override
          public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
              throws Exception {
            super.afterConnectionClosed(session, closeStatus);
            // WS 连接建立后
            log.info("连接断开 " + session);
          }
        };
      }
    });
  }

}
