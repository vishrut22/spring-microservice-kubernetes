package com.dailycodebuffer.OrderService.service;


import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;

public interface OrderService {
    public long placeOrder(OrderRequest orderRequest);
    public OrderResponse getOrderDetails(long orderId);
}
