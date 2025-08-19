package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    // Use the Eureka service ID instead of localhost:port
    private static final String INVENTORY_BASE = "http://INVENTORY-SERVICE/api/inventory/products";

    public OrderServiceImpl(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Order placeOrder(Order order) {
        // 1) Check current stock from Inventory
        String stockUrl = INVENTORY_BASE + "/" + order.getProductId() + "/stock";
        Integer currentStock = restTemplate.getForObject(stockUrl, Integer.class);

        if (currentStock != null && currentStock >= order.getQuantity()) {
            // 2) Update stock in Inventory
            int newStock = currentStock - order.getQuantity();
            String updateUrl = INVENTORY_BASE + "/" + order.getProductId() + "/stock?newStock=" + newStock;
            restTemplate.put(updateUrl, null);

            // 3) Confirm order
            if (order.getTotalPrice() == null) {
                order.setTotalPrice(order.getQuantity() * 100.0); // simple placeholder pricing
            }
            order.setStatus("CONFIRMED");
        } else {
            // Not enough stock → reject
            if (order.getTotalPrice() == null) {
                order.setTotalPrice(0.0);
            }
            order.setStatus("REJECTED");
        }

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(updatedOrder.getQuantity());
                    existing.setTotalPrice(updatedOrder.getTotalPrice());
                    existing.setStatus(updatedOrder.getStatus());
                    return orderRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        });
    }
}
