package com.dailycodebuffer.ProductService.controller;

import com.dailycodebuffer.ProductService.exception.ProductServiceCustomException;
import com.dailycodebuffer.ProductService.model.ErrorResponse;
import com.dailycodebuffer.ProductService.model.ProductRequest;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import com.dailycodebuffer.ProductService.service.ProductService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RateLimiter(name="serviceA")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Void> addProduct(@RequestBody ProductRequest productRequest) {
        productService.addProduct(productRequest);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @RateLimiter(name="serviceA")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long productId) {
        ProductResponse productResponse = productService.getProductById(productId);
        return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
    }

    @ExceptionHandler(ProductServiceCustomException.class)
    private ResponseEntity<ErrorResponse> handleNotFoundException(ProductServiceCustomException ne) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().errorMessage(ne.getMessage())
                .errorCode(ne.getErrorCode()).build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/reduceQuantity/{id}")
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    public ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam("quantity") long quantity) {
        productService.reduceQuantity(productId,quantity);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    private ResponseEntity<ErrorResponse> handleRequestNotPermittedException(RequestNotPermitted ne) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().errorMessage(ne.getMessage())
                .errorCode(ne.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }
}
