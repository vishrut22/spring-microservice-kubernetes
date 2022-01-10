package com.dailycodebuffer.PaymentService.service;

import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.model.PaymentResponse;

public interface PaymentService {
    public long doPayment(PaymentRequest paymentRequest);
    public PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
