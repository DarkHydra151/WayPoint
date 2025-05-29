package com.logistics.platform.service;

import com.logistics.platform.domain.OrderDTO;
import com.logistics.platform.exception.OrderNotFoundException;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDTO> findAllOrders();
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO findOrderById(UUID id) throws OrderNotFoundException;
    List<OrderDTO> findOrdersByClient(UUID clientId);
    OrderDTO updateOrderStatus(UUID id, String status) throws OrderNotFoundException;
    void deleteOrder(UUID id) throws OrderNotFoundException;
}
