package com.dailycodebuffer.OrderService;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.Module;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
public class OrderServiceWireMockConfig {


    @Bean
    public WireMockServer mockInternalMicroServices() {
        return new WireMockServer(options().dynamicPort());
    }

   // @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(){
        return new TestServiceInstanceListSupplier();
    }

    @Bean
    public Module dateTimeModule(){
        return new JavaTimeModule();
    }



}
