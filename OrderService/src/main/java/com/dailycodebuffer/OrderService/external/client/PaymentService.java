package com.dailycodebuffer.OrderService.external.client;

import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient( name = "PAYMENT-SERVICE/payment")
public interface PaymentService {
    @RequestMapping(method = RequestMethod.POST)
    public void doPayment(@RequestBody PaymentRequest paymentRequest);
}
