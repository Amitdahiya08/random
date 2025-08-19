package com.example.orderservice.service;

import com.example.orderservice.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order placeOrder(Order order);
    Optional<Order> getOrderById(Long id);
    List<Order> getAllOrders();
    Order updateOrder(Long id, Order updatedOrder);
    void cancelOrder(Long id);
}
