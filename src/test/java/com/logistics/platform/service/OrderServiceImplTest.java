package com.logistics.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logistics.platform.domain.OrderDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.entity.UserEntity;
import com.logistics.platform.exception.OrderNotFoundException;
import com.logistics.platform.exception.UserNotFoundException;
import com.logistics.platform.repository.OrderRepository;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    private OrderServiceImpl orderService;

    private UUID orderId, clientId;
    private OrderEntity orderEntity;
    private UserEntity client;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, userRepository);
        orderId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        client = UserEntity.builder()
                .id(clientId)
                .email("client@example.com")
                .username("clientUser")
                .build();

        orderEntity = OrderEntity.builder()
                .id(orderId)
                .client(client)
                .status("PENDING")
                .origin("Warehouse A")
                .destination("Client Address")
                .build();

        orderDTO = OrderDTO.builder()
                .id(orderId)
                .clientId(clientId)
                .status("PENDING")
                .origin("Warehouse A")
                .destination("Client Address")
                .build();
    }

    @Test
    void testFindAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(orderEntity));

        List<OrderDTO> result = orderService.findAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(orderRepository).findAll();
    }

    @Test
    void testCreateOrder() {
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void testCreateOrderClientNotFound() {
        when(userRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.createOrder(orderDTO));
    }

    @Test
    void testFindOrderById() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        OrderDTO result = orderService.findOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testFindOrderByIdNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(orderId));
    }

    @Test
    void testFindOrdersByClient() {
        when(orderRepository.findByClientId(clientId)).thenReturn(List.of(orderEntity));

        List<OrderDTO> result = orderService.findOrdersByClient(clientId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clientId, result.get(0).getClientId());
        verify(orderRepository).findByClientId(clientId);
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDTO result = orderService.updateOrderStatus(orderId, "IN_TRANSIT");

        assertNotNull(result);
        assertEquals("IN_TRANSIT", result.getStatus());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void testUpdateOrderStatusNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(orderId, "IN_TRANSIT"));
    }

    @Test
    void testDeleteOrder() {
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.deleteOrder(orderId);

        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void testDeleteOrderNotFound() {
        when(orderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderId));
    }
}
