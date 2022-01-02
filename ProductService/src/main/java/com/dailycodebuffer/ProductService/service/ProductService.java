package com.dailycodebuffer.ProductService.service;

import com.dailycodebuffer.ProductService.model.ProductRequest;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import org.springframework.stereotype.Service;

public interface ProductService {
    public void addProduct(ProductRequest productRequest);
    public ProductResponse getProductById(long productId);
}
