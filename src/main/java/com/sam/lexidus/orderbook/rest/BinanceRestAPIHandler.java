package com.sam.lexidus.orderbook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sam.lexidus.orderbook.model.Order;
import com.sam.lexidus.orderbook.model.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.parseLong;

public class BinanceRestAPIHandler {
    private static final Logger logger = LoggerFactory.getLogger(BinanceRestAPIHandler.class);

    private static final String BINANCE_DEPTH_API_URL = "https://api.binance.com/api/v3/depth?symbol=ETHUSDT&limit=1000";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void getSnapshot(OrderBook orderBook) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(BINANCE_DEPTH_API_URL, String.class);

        handleSnapshot(response, orderBook);
    }

    private static void handleSnapshot(ResponseEntity<String> response, OrderBook orderBook) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());

            long lastUpdateId = parseLong(root.get("lastUpdateId").toString()) ;
            orderBook.setLastUpdateId(lastUpdateId);

            ArrayNode bidArray = (ArrayNode) root.get("bids");
            ArrayNode askArray = (ArrayNode) root.get("asks");

            List<Order> bids = new ArrayList<>();
            for(int i=0; i < bidArray.size(); i++) {
                BigDecimal price = new BigDecimal(bidArray.get(i).get(0).textValue());
                BigDecimal size = new BigDecimal(bidArray.get(i).get(1).textValue());
                Order order = new Order(size, price);
                bids.add(order);
            }
            orderBook.setBids(bids);

            List<Order> asks = new ArrayList<>();
            for(int i=0; i < askArray.size(); i++) {
                BigDecimal price = new BigDecimal(askArray.get(i).get(0).textValue());
                BigDecimal size = new BigDecimal(askArray.get(i).get(1).textValue());
                Order order = new Order(size, price);
                asks.add(order);
            }
            orderBook.setAsks(asks);
        } catch (Exception e) {
            logger.error("Unable to parse snapshot response", e);
            System.exit(0);
        }
    }
}
