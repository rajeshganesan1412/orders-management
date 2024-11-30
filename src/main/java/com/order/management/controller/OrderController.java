package com.order.management.controller;

import com.order.management.model.Orders;
import com.order.management.service.OrderServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderServiceInterface orderServiceInterface;

    @GetMapping("/order/{cartId}")
    public Orders placeOrder(@PathVariable Long cartId) {
        log.info("Entering into place order API");
       return orderServiceInterface.placeOrder(cartId);
    }

}
