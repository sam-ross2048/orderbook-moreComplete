package com.sam.lexidus.orderbook.configuration;

import com.sam.lexidus.orderbook.service.OrderBookService;
import com.sam.lexidus.orderbook.service.OrderBookServiceImpl;
import com.sam.lexidus.orderbook.websocket.BinanceWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
@EnableScheduling()
public class ClientWebSocketSockJsConfig {

    private static final String BINANCE_WEBSOCKET_URI = "wss://stream.binance.com:9443/ws/ethusdt@depth";

    @Bean
    public OrderBookService orderBookService() {
        return new OrderBookServiceImpl();
    }

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager(
            OrderBookServiceImpl orderBookService
    ) {
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                webSocketClient(),
                webSocketHandler(orderBookService),
                BINANCE_WEBSOCKET_URI
        );
        manager.setAutoStartup(true);
        return manager;
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketHandler webSocketHandler(
            OrderBookServiceImpl orderBookService
    ) {
        return new BinanceWebSocketHandler(
                orderBookService
        );
    }
}
