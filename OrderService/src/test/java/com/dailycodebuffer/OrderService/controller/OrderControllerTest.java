package com.dailycodebuffer.OrderService.controller;

import com.dailycodebuffer.OrderService.OrderServiceWireMockConfig;
import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.model.PaymentMode;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.OrderService.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.recording.RecordSpec;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.StreamUtils.copyToString;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest({"server.port:0"})
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = { OrderServiceWireMockConfig.class })
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8080))
            .build();

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() throws IOException {

        getProductDetailResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();

    }


    private void getProductDetailResponse() throws IOException {
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/product/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetProduct.json"),
                                        defaultCharset()))));
    }

    private void getPaymentDetails() throws IOException {
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/payment/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetPayment.json"),
                                        defaultCharset()))));
    }

    private void reduceQuantity() throws IOException {
        wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/product/reduceQuantity/.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void doPayment() throws IOException {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/payment"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private OrderRequest getMockOrderRequest(){
        return OrderRequest.builder().paymentMode(PaymentMode.CASH).productId(1).quantity(10).totalAmount(200).build();
    }
    @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        //First place order
        // Get Order by order id from database and check
        // check output
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(post("/order/placeOrder")
                .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

        Optional<Order> optionalOrder = orderRepository.findById(Long.parseLong(orderId));
        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(Long.parseLong(orderId) , order.getId());
        assertEquals("PLACED", order.getOrderStatus());
        assertEquals(orderRequest.getTotalAmount() , order.getAmount());
        assertEquals(orderRequest.getQuantity() , order.getQuantity());
    }

    @Test
    public void test_WhenPlaceOrderWithWrongAccess_thenThrow403() throws Exception {
        //First place order
        // Get Order by order id from database and check
        // check output
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(post("/order/placeOrder")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_WhenGetOrder_Success() throws Exception {
        //First place order
        // Get Order by order id from database and check
        // check output
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(get("/order/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        Order order =orderRepository.findById(1L).get();
        String expectedResponse = getOrderResponse(order);
        assertEquals(expectedResponse ,actualResponse);
    }

    @Test
    public void test_WhenGetOrder_OrderNotFound() throws Exception {
        //First place order
        // Get Order by order id from database and check
        // check output
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(get("/order/2")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private String getOrderResponse(Order order) throws IOException {
        OrderResponse.PaymentDetails paymentDetails = objectMapper.readValue(copyToString(
                OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetPayment.json"),
                defaultCharset()), OrderResponse.PaymentDetails.class);
        paymentDetails.setPaymentStatus("SUCCESS");

        OrderResponse.ProductDetails productDetails = objectMapper.readValue(copyToString(
                OrderControllerTest.class.getClassLoader().getResourceAsStream("mock/GetProduct.json"),
                defaultCharset()), OrderResponse.ProductDetails.class);


        OrderResponse orderResponse = OrderResponse.builder().paymentDetails(paymentDetails).productDetails(productDetails)
                .orderStatus(order.getOrderStatus()).orderId(order.getId()).amount(order.getAmount()).orderDate(order.getOrderDate()).build();
        return objectMapper.writeValueAsString(orderResponse);
    }


}
