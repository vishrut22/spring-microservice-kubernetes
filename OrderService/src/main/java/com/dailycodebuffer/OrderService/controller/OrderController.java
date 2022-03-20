package com.dailycodebuffer.OrderService.controller;

import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.response.ErrorResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Log4j2
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/placeOrder")
    @PreAuthorize("hasAuthority('Customer')")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order ID: "+orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<ErrorResponse> handleNotFoundException(CustomException ne) {
        return new ResponseEntity<ErrorResponse>(ErrorResponse.builder().errorMessage(ne.getMessage())
                .errorCode(ne.getErrorCode()).build(), HttpStatus.valueOf(ne.getStatus()));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable("orderId") long orderId) {
        OrderResponse orderDetails = orderService.getOrderDetails(orderId);
        return new ResponseEntity<OrderResponse>(orderDetails, HttpStatus.OK);
    }
}
