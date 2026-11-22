package com.example.lab3.service;

import com.example.lab3.dto.OrderDto;
import com.example.lab3.dto.OrderItemDto;
import com.example.lab3.entity.Order;
import com.example.lab3.entity.OrderItem;
import com.example.lab3.entity.Product;
import com.example.lab3.repository.OrderRepository;
import com.example.lab3.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDto create(OrderDto dto) {
        Order order = new Order();
        order.setOrderDate(dto.orderDate() == null ? LocalDateTime.now() : dto.orderDate());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDto itemDto : dto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemDto.productId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.quantity());
            item.setOrder(savedOrder);
            items.add(item);
        }

        savedOrder.setItems(items);
        savedOrder = orderRepository.save(savedOrder);

        return toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return toDto(order);
    }

    private OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity()))
                .toList();

        return new OrderDto(order.getId(), order.getOrderDate(), itemDtos);
    }
}
