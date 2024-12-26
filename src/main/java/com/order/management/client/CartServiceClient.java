package com.order.management.client;

import com.order.management.exception.ClientConnectionException;
import com.order.management.exception.ErrorResponse;
import com.order.management.model.Cart;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    public Cart getCartFromCartService(Long cartId) {
        log.info("Entering into method for calling cart service for cartId {}", cartId);
        return getWebclient(cartServiceUrl, cartId)
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
                            log.error("{} exception occurred while calling cart service", clientResponse.statusCode());
                            return Mono.error(new ClientConnectionException(
                                            "exception occurred while calling cart service", clientResponse.statusCode()));
                        });
    }

}
