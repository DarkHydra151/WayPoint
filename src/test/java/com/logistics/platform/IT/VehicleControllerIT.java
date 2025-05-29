package com.logistics.platform.IT;

import com.logistics.platform.config.TestDataInitializer;
import com.logistics.platform.domain.VehicleDTO;
import com.logistics.platform.entity.VehicleEntity;
import com.logistics.platform.repository.RouteRepository;
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
public class VehicleControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    @Autowired
    private RouteRepository routeRepository;

    private String adminToken;
    private UUID vehicleId;
    private UUID driverId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        vehicleId = testData.vehicleId();
        driverId = testData.adminId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldCreateVehicle() {
        VehicleEntity newVehicle = VehicleEntity.builder()
                .type("VAN")
                .licensePlate("XYZ-789")
                .capacity(3000.0f)
                .currentLocation("Warehouse B")
                .build();

        webTestClient.post()
                .uri("/api/v1/vehicles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newVehicle)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleDTO.class)
                .consumeWith(response -> {
                    VehicleDTO vehicle = response.getResponseBody();
                    assertNotNull(vehicle);
                    assertEquals("VAN", vehicle.getType());
                });
    }

    @Test
    void shouldFailToCreateVehicleWithoutLicensePlate() {
        VehicleEntity invalidVehicle = VehicleEntity.builder()
                .type("TRUCK")
                .capacity(5000.0f)
                .currentLocation("Warehouse A")
                .build();

        webTestClient.post()
                .uri("/api/v1/vehicles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidVehicle)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("License plate must be provided");
    }

    @Test
    void shouldAssignDriverToVehicle() {

        webTestClient.put()
                .uri("/api/v1/vehicles/{id}/assign-driver/{driverId}", vehicleId, driverId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleDTO.class)
                .consumeWith(response -> {
                    VehicleDTO vehicle = response.getResponseBody();
                    assertNotNull(vehicle);
                    assertEquals(driverId, vehicle.getDriverId());
                });
    }

    @Test
    void shouldFailToAssignDriverWithoutAdminRights() {
        UUID driverId = dataInitializer.initTestData().adminId();
        String clientToken = dataInitializer.initTestData().clientToken();

        webTestClient.put()
                .uri("/api/v1/vehicles/{id}/assign-driver/{driverId}", vehicleId, driverId)
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
    void shouldDeleteVehicle() {
        routeRepository.deleteAll();

        webTestClient.delete()
                .uri("/api/v1/vehicles/{id}", vehicleId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }
}
