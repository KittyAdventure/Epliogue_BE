package com.team1.epilogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket// 웹소켓 서버 사용
@EnableWebSocketMessageBroker// STOMP 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    // 클라이언트에서 메세지를 구독하는 경로 (예: /topic/chat)
    registry.enableSimpleBroker("/topic"); // 클라이언트가 구독할 prefix

    // 클라이언트에서 메세지를 보낼 때 사용하는 경로 (예 /app/chat)
    registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메세지 보낼 때 prefix

    //클라이언트에서 메세지를 보냄 -> 사용하는 경로 (/app/chat/sendMessage)로 메세지를 보냄 -> @MessageMapping("/chat.sendMessage")가 처리함
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint("/ws/chat") //ws:localhost:8080/ws/chat
        .setAllowedOriginPatterns("*") // 실제 환경에선 API 서버 도메인만 허용, 모든 오리진 허용(CORS 문제 방지)
        .withSockJS(); // JS 라이브러리, SocketJS를 활성화하여 웹소켓을 지원하지 않은 브라우저에서도 연결 가능
  }
}
