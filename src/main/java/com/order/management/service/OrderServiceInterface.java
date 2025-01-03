package com.order.management.service;


import com.order.management.model.Orders;

public interface OrderServiceInterface {

    Orders placeOrder(Long cartId);

    Orders getOrdersById(Long orderId);

    Orders getOrdersByUserId(String userId);

    Orders cancelOrder(Long orderId);
}
