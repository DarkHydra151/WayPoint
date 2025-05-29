package com.logistics.platform.service.impl;

import com.logistics.platform.domain.OrderDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.exception.OrderNotFoundException;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.repository.OrderRepository;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        UserEntity client = userRepository.findById(orderDTO.getClientId())
                .orElseThrow(() -> new UserNotFoundException("Client not found with id: " + orderDTO.getClientId()));

        OrderEntity order = OrderEntity.builder()
                .client(client)
                .status(orderDTO.getStatus())
                .origin(orderDTO.getOrigin())
                .destination(orderDTO.getDestination())
                .estimatedDeliveryTime(orderDTO.getEstimatedDeliveryTime())
                .build();

        OrderEntity savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findOrderById(UUID id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findOrdersByClient(UUID clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(UUID id, String status) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        order = order.toBuilder()
                .status(status)
                .build();

        OrderEntity updatedOrder = orderRepository.save(order);
        return mapToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private OrderDTO mapToDTO(OrderEntity order) {
        return OrderDTO.builder()
                .id(order.getId())
                .clientId(order.getClient().getId())
                .status(order.getStatus())
                .origin(order.getOrigin())
                .destination(order.getDestination())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .build();
    }
}
