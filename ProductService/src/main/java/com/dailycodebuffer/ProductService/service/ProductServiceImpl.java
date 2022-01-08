package com.dailycodebuffer.ProductService.service;

import com.dailycodebuffer.ProductService.entity.Product;
import com.dailycodebuffer.ProductService.exception.ProductServiceCustomException;
import com.dailycodebuffer.ProductService.model.ProductRequest;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import com.dailycodebuffer.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void addProduct(ProductRequest productRequest) {
        log.info("Adding product.");
        productRepository.save(Product.builder().productName(productRequest.getName())
                .quantity(productRequest.getQuantity()).price(productRequest.getPrice()).build());
        log.info("Product Created");
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get product for id {}", productId);
        Optional<Product> productOpt = productRepository.findById(productId);
        Product product = productOpt.orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = ProductResponse.builder().productId(product.getId()).productName(product.getProductName())
                .price(product.getPrice()).quantity(product.getQuantity()).build();
        log.info("Product response is :{}",productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for id {}",quantity,productId);
        Optional<Product> productOpt = productRepository.findById(productId);
        Product product = productOpt.orElseThrow(() -> new ProductServiceCustomException("Product with given id not found","PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity){
            throw new ProductServiceCustomException("Product does not have sufficient quantity.","INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully.");
    }
}
