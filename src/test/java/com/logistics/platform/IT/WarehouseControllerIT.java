package com.logistics.platform.IT;

import com.logistics.platform.config.TestDataInitializer;
import com.logistics.platform.domain.WarehouseDTO;
import com.logistics.platform.entity.WarehouseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WarehouseControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID warehouseId;
    private UUID managerId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        warehouseId = testData.warehouseId();
        managerId = testData.adminId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldCreateWarehouse() {
        WarehouseDTO newWarehouse = WarehouseDTO.builder()
                .managerId(managerId)
                .location("New Storage Facility")
                .capacity(2000)
                .availableSpace(1500)
                .build();

        webTestClient.post()
                .uri("/api/v1/warehouses")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newWarehouse)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WarehouseDTO.class)
                .consumeWith(response -> {
                    WarehouseDTO warehouse = response.getResponseBody();
                    assertNotNull(warehouse);
                    assertEquals("New Storage Facility", warehouse.getLocation());
                });
    }

    @Test
    void shouldFailToCreateWarehouseWithoutLocation() {
        WarehouseEntity invalidWarehouse = WarehouseEntity.builder()
                .capacity(3000)
                .availableSpace(2500)
                .build();

        webTestClient.post()
                .uri("/api/v1/warehouses")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidWarehouse)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Location must be provided");
    }

    @Test
    void shouldUpdateWarehouseDetails() {
        WarehouseEntity updatedWarehouse = WarehouseEntity.builder()
                .location("Updated Facility")
                .capacity(5000)
                .availableSpace(4500)
                .build();

        webTestClient.put()
                .uri("/api/v1/warehouses/{id}", warehouseId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedWarehouse)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WarehouseDTO.class)
                .consumeWith(response -> {
                    WarehouseDTO warehouse = response.getResponseBody();
                    assertNotNull(warehouse);
                    assertEquals("Updated Facility", warehouse.getLocation());
                });
    }

    @Test
    void shouldDeleteWarehouse() {
        webTestClient.delete()
                .uri("/api/v1/warehouses/{id}", warehouseId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }
}
