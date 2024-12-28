package com.order.management.service;

import com.order.management.client.CartServiceClient;
import com.order.management.exception.EmptyCartItemsException;
import com.order.management.exception.OrderNotFoundException;
import com.order.management.model.Cart;
import com.order.management.model.Orders;
import com.order.management.model.OrderItems;
import com.order.management.producer.OrderMessageProducer;
import com.order.management.repository.OrderItemRepository;
import com.order.management.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final CartServiceClient cartServiceClient;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderMessageProducer orderMessageProducer;

    @Override
    public Orders placeOrder(Long cartId) {
        log.info("Entering into place order method for the cartId {}", cartId);
        // Calling cart service to get the cart details for this cartId
        Cart cart = cartServiceClient.getCartFromCartService(cartId);
        if (cart != null && !cart.getCartProducts().isEmpty()) {
            log.info("Building order details to save into order DB and cart detail is {}", cart);
            List<OrderItems> orderItemsList = orderItemRepository.saveAll(buildOrderItems(cart));
            Orders order = Orders.builder()
                    .userId(cart.getUserId())
                    .orderItems(orderItemsList)
                    .totalCost(calculateTotalCost(cart))
                    .orderDate(LocalDate.now())
                    .build();
            Orders orders = orderRepository.save(order);
            orderMessageProducer.sendMessage(orders);
            return orders;

        }
        else {
            throw new EmptyCartItemsException("No items found in cart. Could not place order", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Orders getOrdersById(Long orderId) {
        log.info("Get orders by id");
        return Optional.of(orderRepository.findById(orderId)).get().orElseThrow(()->
                new OrderNotFoundException("No Orders found for this order id", HttpStatus.NOT_FOUND));
    }

    @Override
    public Orders getOrdersByUserId(String userId) {
        log.info("Get orders by user id");
        return Optional.of(orderRepository.findByUserId(userId)).get().orElseThrow(()->
                new OrderNotFoundException("No Orders found for this order id", HttpStatus.NOT_FOUND));
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
                    .productId(cartProduct.getCartProductId())
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
