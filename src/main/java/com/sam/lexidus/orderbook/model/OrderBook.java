package com.sam.lexidus.orderbook.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

// Room for improvement here.
@Data
public class OrderBook {
    private long lastUpdateId;
    private List<Order> bids = new ArrayList<>();
    private List<Order> asks = new ArrayList<>();

    public synchronized void updateBidOrder(Order bid) {
        Integer index = Collections.binarySearch(bids.stream().map(Order::getPrice).toList(), bid.getPrice());

        if (index >= 0) {
            bids.remove(bids.get(index));
        } else {
            index = -index - 1;
        }
        if (bid.getSize().compareTo(BigDecimal.ZERO) != 0) {
            bids.add(index, bid);
        }
    }

    public synchronized void updateAskOrder(Order ask) {
        Integer index = Collections.binarySearch(asks.stream().map(Order::getPrice).toList(), ask.getPrice());
        if (index >= 0) {
            asks.remove(asks.get(index));
        } else {
            index = -index - 1;
        }
        if (ask.getSize().compareTo(BigDecimal.ZERO) != 0) {
            asks.add(index, ask);
        }
    }

    public synchronized void sort() {
        bids.sort(Comparator.comparing(order -> order.getPrice().negate()));
        asks.sort(Comparator.comparing(Order::getPrice));
    }
}
