package com.sam.lexidus.orderbook.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sam.lexidus.orderbook.model.Order;
import com.sam.lexidus.orderbook.service.OrderBookService;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BinanceWebSocketHandler extends TextWebSocketHandler {
    private OrderBookService orderBookService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private long prevFinalUpdate;

    public BinanceWebSocketHandler(OrderBookService orderBookService) {
        super();
        this.orderBookService = orderBookService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        orderBookService.start();
        prevFinalUpdate = orderBookService.getOrderBook().getLastUpdateId();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO validate that the event is in sequence.
        JsonNode payload = objectMapper.readTree((String) message.getPayload());

        ArrayNode bidArray = (ArrayNode) payload.get("b");
        ArrayNode askArray = (ArrayNode) payload.get("a");

        List<Order> bids = new ArrayList<>();
        for (int i = 0; i < bidArray.size(); i++) {
            BigDecimal price = new BigDecimal(bidArray.get(i).get(0).textValue());
            BigDecimal size = new BigDecimal(bidArray.get(i).get(1).textValue());
            Order order = new Order(size, price);
            bids.add(order);
        }

        List<Order> asks = new ArrayList<>();
        for(int i=0; i<askArray.size();i++) {
            BigDecimal price = new BigDecimal(askArray.get(i).get(0).textValue());
            BigDecimal size =  new BigDecimal(askArray.get(i).get(1).textValue());
            Order order = new Order(size, price);
            asks.add(order);
        }

        orderBookService.updateOrderBook(bids, asks);
    }
}
