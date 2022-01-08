package com.dailycodebuffer.OrderService.external.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ProductResponse {
    private long productId;
    private String productName;
    private long quantity;
    private long price;
}