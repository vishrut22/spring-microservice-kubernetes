package com.dailycodebuffer.OrderService.service;


import com.dailycodebuffer.OrderService.model.OrderRequest;

public interface OrderService {
    public void placeOrder(OrderRequest orderRequest);
}
