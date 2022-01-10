package com.dailycodebuffer.OrderService.external.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductResponse {
    private long productId;
    private String productName;
    private long quantity;
    private long price;
}