package com.dailycodebuffer.OrderService.external.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient( name = "PAYMENT-SERVICE")
public interface PaymentService {
}
