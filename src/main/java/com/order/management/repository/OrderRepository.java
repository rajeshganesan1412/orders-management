package com.order.management.repository;

import com.order.management.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    public Optional<Orders> findByUserId(String userId);
}
