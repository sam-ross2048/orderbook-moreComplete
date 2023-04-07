package com.sam.lexidus.orderbook.service;

import com.sam.lexidus.orderbook.model.Order;
import com.sam.lexidus.orderbook.model.OrderBook;

import java.util.List;

public interface OrderBookService {

    public void start();

    public OrderBook getOrderBook();

    public void updateOrderBook(List<Order> bids, List<Order> asks);
}
