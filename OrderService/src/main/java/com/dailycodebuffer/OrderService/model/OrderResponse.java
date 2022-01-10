package com.dailycodebuffer.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private long orderId;
    private Instant orderDate;
    private String orderStatus;
    private ProductDetails productDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class ProductDetails{
        private String productName;
        private long productId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class PaymentDetails{
        private long paymentId;
        private PaymentMode paymentMode;
        private String paymentStatus;
        private Instant paymentDate;
    }
}
