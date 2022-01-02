package com.dailycodebuffer.ProductService.model;

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
