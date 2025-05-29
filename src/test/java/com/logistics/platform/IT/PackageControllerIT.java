package com.logistics.platform.IT;

import com.logistics.platform.config.TestDataInitializer;
import com.logistics.platform.domain.PackageDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.entity.PackageEntity;
import com.logistics.platform.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PackageControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID packageId;
    private UUID orderId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        packageId = testData.packageId();
        orderId = testData.orderId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldCreatePackage() {
        PackageDTO newPackageDTO = PackageDTO.builder()
                .orderId(orderId)
                .description("Fragile Goods")
                .weight(10.0f)
                .status("PACKED")
                .build();

        webTestClient.post()
                .uri("/api/v1/packages")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newPackageDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageDTO.class)
                .consumeWith(response -> {
                    PackageDTO packageDTO = response.getResponseBody();
                    assertNotNull(packageDTO);
                    assertEquals("PACKED", packageDTO.getStatus());
                });
    }

    @Test
    void shouldFailToCreatePackageWithoutOrder() {
        PackageEntity invalidPackage = PackageEntity.builder()
                .description("Electronics")
                .weight(5.0f)
                .status("PACKED")
                .build();

        webTestClient.post()
                .uri("/api/v1/packages")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidPackage)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Order ID must be provided");
    }

    @Test
    void shouldUpdatePackageStatus() {
        webTestClient.put()
                .uri("/api/v1/packages/{id}", packageId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(PackageDTO.builder()
                        .id(packageId)
                        .orderId(orderId)
                        .description("Updated Package")
                        .weight(15.0f)
                        .status("IN_TRANSIT")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PackageDTO.class)
                .consumeWith(response -> {
                    PackageDTO packageDTO = response.getResponseBody();
                    assertNotNull(packageDTO);
                    assertEquals("IN_TRANSIT", packageDTO.getStatus());
                });
    }

    @Test
    void shouldFailToUpdatePackageStatusWithoutAdminRights() {
        String clientToken = dataInitializer.initTestData().clientToken();

        webTestClient.put()
                .uri("/api/v1/packages/{id}", packageId)
                .header("Authorization", "Bearer " + clientToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(PackageDTO.builder()
                        .id(packageId)
                        .orderId(orderId)
                        .description("Updated Package")
                        .weight(15.0f)
                        .status("IN_TRANSIT")
                        .build())
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
    void shouldDeletePackage() {
        webTestClient.delete()
                .uri("/api/v1/packages/{id}", packageId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }
}
