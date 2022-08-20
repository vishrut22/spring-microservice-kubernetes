package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.response.PaymentResponse;
import com.dailycodebuffer.OrderService.external.response.ProductResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservice.product}")
    private String productServiceUrl;

    @Value("${microservice.payment}")
    private String paymentServiceUrl;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing Order Request {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.debug("Creating order with status CREATED.");
        Order order = Order.builder().amount(orderRequest.getTotalAmount()).orderDate(Instant.now())
                .orderStatus("CREATED").productId(orderRequest.getProductId()).quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.debug("Calling payment service to make payment.");
        PaymentRequest paymentRequest = PaymentRequest.builder().orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();
        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.debug("Payment done successfully. Changing orderstatus to PLACED.");
            orderStatus = "PLACED";

        } catch (Exception e) {
            log.warn("Error occurred in payment.Changing order status to PAYMENT_FAILED.");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order placed succesfully with order id : {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details : {}", orderId);
        Optional<Order> optOrderResponse = orderRepository.findById(orderId);
        Order order = optOrderResponse.orElseThrow(() -> new CustomException("Order not found.", "ORDER_NOT_FOUND", 404));
        log.debug("Invoking get Product using product service from product id : {}", order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject(productServiceUrl +"/"+ order.getProductId(), ProductResponse.class);
        log.debug("Invoking get Payment details using payment service");
        PaymentResponse paymentResponse = restTemplate.getForObject(paymentServiceUrl +"/?orderId=" + orderId, PaymentResponse.class);
        log.debug("Preparing response for get order.");
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productId(productResponse.getProductId())
                .productName(productResponse.getProductName()).build();
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate()).paymentMode(paymentResponse.getPaymentMode()).build();
        OrderResponse orderResponse = OrderResponse.builder().productDetails(productDetails)
                .paymentDetails(paymentDetails).orderId(orderId)
                .orderDate(order.getOrderDate()).orderStatus(order.getOrderStatus()).amount(order.getAmount()).build();
        log.info("Order details are : {}", orderResponse);
        return orderResponse;
    }
}
