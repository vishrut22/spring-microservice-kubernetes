package com.dailycodebuffer.PaymentService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private long orderId;
    private PaymentMode paymentMode;
    private long amount;
    private String referenceNumber;
}
