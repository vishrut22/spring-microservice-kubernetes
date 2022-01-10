package com.dailycodebuffer.OrderService.external.client;

import com.dailycodebuffer.OrderService.external.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/product")
public interface ProductService {
    @RequestMapping(method = RequestMethod.GET, value ="{id}")
    public ProductResponse getProducts(@PathVariable("id") long id);

    @RequestMapping(method = RequestMethod.PUT, value ="/reduceQuantity/{id}/")
    public void reduceQuantity(@PathVariable("id") long id, @RequestParam("quantity") long quantity);

}
