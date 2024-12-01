package com.order.management.client;

import com.order.management.exception.ClientConnectionException;
import com.order.management.exception.ErrorResponse;
import com.order.management.model.Cart;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @CircuitBreaker(name="orderServiceCircuitBreaker", fallbackMethod = "fallBackProductService")
    public Cart getCartFromProductService(Long cartId) {
        log.info("Entering into method for calling product service for cartId {}", cartId);
        return getWebclient(productServiceUrl, cartId)
                .get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .bodyToMono(Cart.class)
                .block();
    }

    private WebClient getWebclient(String url, Long cartId) {
        return webClientBuilder
                .clone()
                .baseUrl(url+"/cart/"+cartId)
                .build();
    }

    private Mono<Throwable> handleError(ClientResponse clientResponse) {
        log.info("Handling communication error");
        return clientResponse
                .bodyToMono(ErrorResponse.class)
                .flatMap(errorspec -> {
                            log.error("{} exception occurred while calling product service", clientResponse.statusCode());
                            return Mono.error(new ClientConnectionException(
                                            "exception occurred while calling product service", clientResponse.statusCode()));
                        });
    }

    public Mono<Cart> fallBackProductService(Long cartId, Throwable e) {
        // You can log the error, notify, or return a default response
        return Mono.error(new ClientConnectionException("Could not connect to product service", HttpStatus.SERVICE_UNAVAILABLE));
    }

//    @CircuitBreaker(name="orderServiceCircuitBreaker", fallbackMethod = "fallBackProductService")
//    public Cart getCartFromProductService(Long cartId) {
//        log.info("Entering into method for calling product service for cartId {}", cartId);
//      return webClient
//              .get()
//              .uri("/cart/{cartId}", cartId)
//              .retrieve()
//              .onStatus(HttpStatusCode::isError, this::handleError)
//              .bodyToMono(Cart.class)
//              .block();
//    }
//
//    private Mono<Throwable> handleError(ClientResponse clientResponse) {
//        log.info("Handling communication error");
//        return clientResponse.bodyToMono(ErrorResponse.class)
//                .flatMap(errorResponse -> {
//                    log.error("{} exception occurred while calling product service", clientResponse.statusCode());
//                    return Mono.error(new ClientConnectionException("Could not connect to product service", clientResponse.statusCode()));
//                });
//    }
//
//    public Mono<Cart> fallBackProductService(Long cartId, Throwable e) {
//        // You can log the error, notify, or return a default response
//        return Mono.error(new ClientConnectionException("Could not connect to product service", HttpStatus.SERVICE_UNAVAILABLE));
//    }


}
