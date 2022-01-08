package com.dailycodebuffer.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private long productId;
    private PaymentMode paymentMode;
    private long totalAmount;
    private long quantity;
}
