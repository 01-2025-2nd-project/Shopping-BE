package com.github.farmplus.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("WebSocket endpoint '/ws' 등록됨");
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                        "https://frontend-ecru-phi-22.vercel.app",  // 배포된 프론트엔드 URL
                        "http://localhost:3000",                    // 로컬 환경에서 실행되는 프론트엔드 URL (예: React 앱)
                        "https://*.ngrok.io",                       // ngrok URL 패턴 (동적 URL 허용)
                        "https://jiangxy.github.io" ,
                        "http://localhost:8080"// WebSocket 테스트 툴의 출처 추가
                );
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
