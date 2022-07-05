package com.dailycodebuffer.cloudgateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
@EnableEurekaClient
public class CloudgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudgatewayApplication.class, args);
	}

	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()).build());
	}

	/*@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.just("1");
	}*/
	@Bean
	KeyResolver authUserKeyResolver() {
		return exchange -> ReactiveSecurityContextHolder.getContext()
				.map(ctx -> ctx.getAuthentication()
						.getCredentials().toString());
	}
//Refresh token
//Expiration time
// Unit test case
// integration test case
// DockerFile - build image publish
// Kubernetes deployment - MySQL
// Cloud deployment using CI/CD
// Monitoring all services

//Spring boot microservice grpc
// Spring boot mongodb
// Microservice Golang
}
