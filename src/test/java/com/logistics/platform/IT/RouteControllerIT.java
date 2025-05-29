package com.logistics.platform.IT;

import com.logistics.platform.config.TestDataInitializer;
import com.logistics.platform.domain.RouteDTO;
import com.logistics.platform.entity.RouteEntity;
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
public class RouteControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID routeId;
    private UUID vehicleId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        routeId = testData.routeId();
        vehicleId = testData.vehicleId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldCreateRoute() {
        RouteDTO newRoute = RouteDTO.builder()
                .vehicleId(vehicleId)
                .origin("Warehouse B")
                .destination("Downtown Hub")
                .estimatedTime("4 hours")
                .trafficConditions("Heavy traffic")
                .build();

        webTestClient.post()
                .uri("/api/v1/routes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(newRoute)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RouteDTO.class)
                .consumeWith(response -> {
                    RouteDTO routeDTO = response.getResponseBody();
                    assertNotNull(routeDTO);
                    assertEquals("Downtown Hub", routeDTO.getDestination());
                });
    }

    @Test
    void shouldFailToCreateRouteWithoutVehicle() {
        RouteEntity invalidRoute = RouteEntity.builder()
                .origin("Warehouse A")
                .destination("Client Location")
                .estimatedTime("6 hours")
                .trafficConditions("Moderate traffic")
                .build();

        webTestClient.post()
                .uri("/api/v1/routes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(invalidRoute)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Vehicle ID must be provided");
    }

    @Test
    void shouldUpdateRoute() {
        RouteDTO updatedRouteDTO = RouteDTO.builder()
                .id(routeId)
                .vehicleId(vehicleId)
                .origin("Updated Warehouse")
                .destination("Updated City Center")
                .estimatedTime("3 hours")
                .trafficConditions("Light traffic")
                .build();

        webTestClient.put()
                .uri("/api/v1/routes/{id}", routeId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedRouteDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RouteDTO.class)
                .consumeWith(response -> {
                    RouteDTO routeDTO = response.getResponseBody();
                    assertNotNull(routeDTO);
                    assertEquals("Updated City Center", routeDTO.getDestination());
                });
    }

    @Test
    void shouldFailToUpdateRouteWithoutAdminRights() {
        String clientToken = dataInitializer.initTestData().clientToken();

        RouteDTO updatedRouteDTO = RouteDTO.builder()
                .id(routeId)
                .vehicleId(vehicleId)
                .origin("Updated Warehouse")
                .destination("Updated City Center")
                .estimatedTime("3 hours")
                .trafficConditions("Light traffic")
                .build();

        webTestClient.put()
                .uri("/api/v1/routes/{id}", routeId)
                .header("Authorization", "Bearer " + clientToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(updatedRouteDTO)
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
    void shouldDeleteRoute() {
        webTestClient.delete()
                .uri("/api/v1/routes/{id}", routeId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }
}
