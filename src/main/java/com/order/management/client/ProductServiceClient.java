package com.order.management.client;

import com.order.management.exception.ClientConnectionException;
import com.order.management.exception.ErrorResponse;
import com.order.management.model.Cart;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class ProductServiceClient {

    private final WebClient webClient;

    public Cart getCartFromProductService(Long cartId) {
        log.info("Entering into method for calling product service for cartId {}", cartId);
      return webClient
              .get()
              .uri("/cart/{cartId}", cartId)
              .retrieve()
              .onStatus(HttpStatusCode::isError, this::handleError)
              .bodyToMono(Cart.class)
              .block();
    }

    private Mono<Throwable> handleError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(ErrorResponse.class)
                .flatMap(errorResponse -> {
                    log.error("{} exception occurred while calling product service", clientResponse.statusCode());
                    return Mono.error(new ClientConnectionException("Could not connect to product service", clientResponse.statusCode()));
                });
    }
}
