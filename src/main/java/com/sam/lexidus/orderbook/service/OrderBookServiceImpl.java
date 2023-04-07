package com.sam.lexidus.orderbook.service;

import com.sam.lexidus.orderbook.model.Order;
import com.sam.lexidus.orderbook.model.OrderBook;
import com.sam.lexidus.orderbook.rest.BinanceRestAPIHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBookServiceImpl implements OrderBookService {

    private final OrderBook orderBook = new OrderBook();

    private Boolean hasStarted = false;

    @Value("${orderBook.numLevels}")
    private Integer numLevels;

    public OrderBookServiceImpl() {}

    public void start() {
        BinanceRestAPIHandler.getSnapshot(orderBook);
        hasStarted = true;
    }

    public void updateOrderBook(List<Order> bids, List<Order> asks) {
        for (Order bid: bids) {
            orderBook.updateBidOrder(bid);
        }
        for (Order ask: asks) {
            orderBook.updateAskOrder(ask);
        }
    }

    @Scheduled(initialDelay = 5000L, fixedDelay = 10000L)
    private void printOrderBook() {
        if (!this.hasStarted) return;

        System.out.printf("%-10s", "BID_SIZE");
        System.out.printf("%10s", "BID_PRICE");
        System.out.print("    ");
        System.out.printf("%-10s", "ASK_PRICE");
        System.out.printf("%10s%n", "ASK_SIZE");

        orderBook.sort();

        for (int i = 0; i < numLevels; i++) {
            System.out.printf("%-10.3f", orderBook.getBids().get(i).getSize());
            System.out.printf("%10.3f", orderBook.getBids().get(i).getPrice());
            System.out.print("    ");
            System.out.printf("%-10.3f", orderBook.getAsks().get(i).getPrice());
            System.out.printf("%10.3f%n", orderBook.getAsks().get(i).getSize());
        }
        System.out.print("\n\n");
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }
}
