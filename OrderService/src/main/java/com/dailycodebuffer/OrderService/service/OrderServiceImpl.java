package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.response.ProductResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        log.info("Placing Order Request {}",orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.debug("Calling payment service to make payment.");
        
    }
}
