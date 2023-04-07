package com.sam.lexidus.orderbook.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private BigDecimal size;
    private BigDecimal price;

    public Order(BigDecimal size, BigDecimal price) {
        this.size = size;
        this.price = price;
    }
}
