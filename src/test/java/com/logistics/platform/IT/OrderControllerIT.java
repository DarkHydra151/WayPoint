package com.logistics.platform.IT;

import com.logistics.platform.config.TestDataInitializer;
import com.logistics.platform.domain.OrderDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    @Autowired
    private PackageRepository packageRepository;

    private String adminToken;
    private String clientToken;
    private UUID orderId;
    private UUID clientId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        orderId = testData.orderId();
        clientId = testData.clientId();
        adminToken = testData.adminToken();
        clientToken = testData.clientToken();
    }

    @Test
    void shouldCreateOrder() {
        OrderDTO newOrder = OrderDTO.builder()
                .clientId(clientId)
                .status("PENDING")
                .origin("Warehouse A")
                .destination("Client Address")
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(3))
                .build();

        webTestClient.post()
                .uri("/api/v1/orders")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newOrder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDTO.class)
                .consumeWith(response -> {
                    OrderDTO order = response.getResponseBody();
                    assertNotNull(order);
                    assertEquals("PENDING", order.getStatus());
                });
    }

    @Test
    void shouldFailToCreateOrderWithoutDestination() {
        OrderDTO invalidOrder = OrderDTO.builder()
                .clientId(clientId)
                .status("PENDING")
                .origin("Warehouse A")
                .destination(null)
                .estimatedDeliveryTime(LocalDateTime.now().plusDays(3))
                .build();

        webTestClient.post()
                .uri("/api/v1/orders")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidOrder)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Destination must be specified");
    }

    @Test
    void shouldUpdateOrderStatus() {
        webTestClient.put()
                .uri("/api/v1/orders/{id}/status?status=IN_TRANSIT", orderId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDTO.class)
                .consumeWith(response -> {
                    OrderDTO order = response.getResponseBody();
                    assertNotNull(order);
                    assertEquals("IN_TRANSIT", order.getStatus());
                });
    }

    @Test
    void shouldFailToUpdateOrderStatusWithoutAdminRights() {
        webTestClient.put()
                .uri("/api/v1/orders/{id}/status?status=DELIVERED", orderId)
                .header("Authorization", "Bearer " + clientToken)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String errorMessage = response.getResponseBody();
                    assertNotNull(errorMessage);
                    assertTrue(errorMessage.contains("Access Denied"));
                });
    }

    @Test
    void shouldFailToUpdateOrderStatusForNonexistentOrder() {
        UUID nonexistentOrderId = UUID.randomUUID();

        webTestClient.put()
                .uri("/api/v1/orders/{id}/status?status=IN_TRANSIT", nonexistentOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order not found with id: " + nonexistentOrderId);
    }

    @Test
    void shouldDeleteOrder() {
        packageRepository.deleteAll();
        webTestClient.delete()
                .uri("/api/v1/orders/{id}", orderId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldFailToDeleteOrderWithoutAdminRights() {
        webTestClient.delete()
                .uri("/api/v1/orders/{id}", orderId)
                .header("Authorization", "Bearer " + clientToken)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String errorMessage = response.getResponseBody();
                    assertNotNull(errorMessage);
                    assertTrue(errorMessage.contains("Access Denied"));
                });
    }

    @Test
    void shouldFailToDeleteNonexistentOrder() {
        UUID nonexistentOrderId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/api/v1/orders/{id}", nonexistentOrderId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order not found with id: " + nonexistentOrderId);
    }
}
