package com.dailycodebuffer.OrderService.external.client;

import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.response.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external" , fallbackMethod = "fallback")
@FeignClient(name = "PRODUCT-SERVICE/product")
public interface ProductService {
    @RequestMapping(method = RequestMethod.GET, value ="{id}")
    public ProductResponse getProducts(@PathVariable("id") long id);

    @RequestMapping(method = RequestMethod.PUT, value ="/reduceQuantity/{id}/")
    public void reduceQuantity(@PathVariable("id") long id, @RequestParam("quantity") long quantity);

    default void fallback(Exception e) {
        throw new CustomException("UNAVAILABLE","Product service not available.",500);
    }
}
