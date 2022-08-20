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
import com.dailycodebuffer.OrderService.model.PaymentMode;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceimplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ProductService productService;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_whenGetOrder_success(){
        //mock
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(restTemplate.getForObject("http://product-service-svc/product/" + order.getProductId(), ProductResponse.class)).thenReturn(getMockProductResponse());
        when(restTemplate.getForObject("http://payment-service-svc/payment/?orderId=" + order.getId(), PaymentResponse.class)).thenReturn(getMockPaymentResponse());

        //act
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        //Check number of calls
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate , times(1)).getForObject("http://product-service-svc/product/" + order.getProductId(), ProductResponse.class);
        verify(restTemplate , times(1)).getForObject("http://payment-service-svc/payment/?orderId=" + order.getId(), PaymentResponse.class);

        //assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());
    }

    @DisplayName("Place Order - Payment Success Scenario")
    @Test
    void test_whenPlaceOrder_success(){
        //mock
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(productService).reduceQuantity(anyLong(), anyLong());
        doNothing().when(paymentService).doPayment(any(PaymentRequest.class));

        //act
        long orderId = orderService.placeOrder(orderRequest);

        //verify
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        //assert
        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place Order - Payment Failure Scenario")
    @Test
    void test_whenPlaceOrderPaymentFail_success(){
        //mock
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(productService).reduceQuantity(anyLong(), anyLong());
        doThrow(new RuntimeException()).when(paymentService).doPayment(any(PaymentRequest.class));

        //act
        long orderId = orderService.placeOrder(orderRequest);

        //verify
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        //assert
        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_whenGetOrderNotFound_thenNotFound(){
        //mock
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        //assert
        CustomException customException = assertThrows(CustomException.class , () -> orderService.getOrderDetails(1));
        assertEquals("ORDER_NOT_FOUND", customException.getErrorCode());
        assertEquals(404, customException.getStatus());
        //Check number of calls
        verify(orderRepository, times(1)).findById(anyLong());
    }

    private Order getMockOrder(){
        return Order.builder().orderStatus("PLACED").orderDate(Instant.now()).id(1).amount(100).quantity(200).productId(2).build();
    }

    private ProductResponse getMockProductResponse(){
        return ProductResponse.builder().productId(2).productName("CLOTHE").price(100).quantity(200).build();
    }

    private PaymentResponse getMockPaymentResponse(){
        return PaymentResponse.builder().paymentId(1).paymentDate(Instant.now()).paymentMode(PaymentMode.CASH).amount(200).orderId(1).status("ACCEPTED").build();
    }

    private OrderRequest getMockOrderRequest(){
        return OrderRequest.builder().paymentMode(PaymentMode.CASH).productId(1).quantity(10).totalAmount(200).build();
    }
}
