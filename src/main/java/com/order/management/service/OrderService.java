package com.order.management.service;

import com.order.management.client.ProductServiceClient;
import com.order.management.model.Cart;
import com.order.management.model.CartProduct;
import com.order.management.model.Orders;
import com.order.management.model.OrderItems;
import com.order.management.repository.OrderItemRepository;
import com.order.management.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final ProductServiceClient productServiceClient;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    @Override
    public Orders placeOrder(Long cartId) {
        log.info("Entering into place order method for the cartId {}", cartId);
        // Calling product service to get the cart details for this cartId
        Cart cart = productServiceClient.getCartFromProductService(cartId);
        log.info("Building order details to save into order DB and cart detail is {}", cart);
        List<OrderItems> orderItemsList = orderItemRepository.saveAll(buildOrderItems(cart));
        Orders order = Orders.builder()
                .orderItems(orderItemsList)
                .totalCost(calculateTotalCost(cart))
                .orderDate(LocalDate.now())
                .build();
        return orderRepository.save(order);
    }

    private BigDecimal calculateTotalCost(Cart cart) {
        log.info("Calculating total cost");
        return cart.getCartProducts().stream()
                .map(cartProduct -> cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItems> buildOrderItems(Cart cart) {
        log.info("Building order items");
        List<OrderItems> orderItemsList = new ArrayList<>();
        cart.getCartProducts().forEach(cartProduct -> {
            OrderItems orderItems = OrderItems.builder()
                    .productName(cartProduct.getProductName())
                    .description(cartProduct.getDescription())
                    .category(cartProduct.getCategory())
                    .price(cartProduct.getPrice())
                    .quantity(cartProduct.getQuantity())
                    .build();
            orderItemsList.add(orderItems);
        });
        return  orderItemsList;
    }
}
