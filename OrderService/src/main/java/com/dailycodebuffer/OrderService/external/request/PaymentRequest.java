package com.dailycodebuffer.OrderService.external.request;

import com.dailycodebuffer.OrderService.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private long orderId;
    private PaymentMode paymentMode;
    private long amount;
    private String referenceNumber;
}
