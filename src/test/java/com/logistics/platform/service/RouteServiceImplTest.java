package com.logistics.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logistics.platform.domain.RouteDTO;
import com.logistics.platform.entity.RouteEntity;
import com.logistics.platform.entity.VehicleEntity;
import com.logistics.platform.exception.RouteNotFoundException;
import com.logistics.platform.exception.VehicleNotFoundException;
import com.logistics.platform.repository.RouteRepository;
import com.logistics.platform.repository.VehicleRepository;
import com.logistics.platform.service.impl.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private VehicleRepository vehicleRepository;

    private RouteServiceImpl routeService;

    private UUID routeId, vehicleId;
    private RouteEntity routeEntity;
    private VehicleEntity vehicle;
    private RouteDTO routeDTO;

    @BeforeEach
    void setUp() {
        routeService = new RouteServiceImpl(routeRepository, vehicleRepository);
        routeId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();

        vehicle = VehicleEntity.builder()
                .id(vehicleId)
                .licensePlate("ABC-123")
                .type("TRUCK")
                .build();

        routeEntity = RouteEntity.builder()
                .id(routeId)
                .vehicle(vehicle)
                .origin("Warehouse A")
                .destination("City Center")
                .estimatedTime("2 hours")
                .trafficConditions("Moderate traffic")
                .build();

        routeDTO = RouteDTO.builder()
                .id(routeId)
                .vehicleId(vehicleId)
                .origin("Warehouse A")
                .destination("City Center")
                .estimatedTime("2 hours")
                .trafficConditions("Moderate traffic")
                .build();
    }

    @Test
    void testFindAllRoutes() {
        when(routeRepository.findAll()).thenReturn(List.of(routeEntity));

        List<RouteDTO> result = routeService.findAllRoutes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("City Center", result.get(0).getDestination());
        verify(routeRepository).findAll();
    }

    @Test
    void testCreateRoute() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(routeRepository.save(any(RouteEntity.class))).thenReturn(routeEntity);

        RouteDTO result = routeService.createRoute(routeDTO);

        assertNotNull(result);
        assertEquals("City Center", result.getDestination());
        verify(routeRepository).save(any(RouteEntity.class));
    }

    @Test
    void testCreateRouteVehicleNotFound() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> routeService.createRoute(routeDTO));
    }

    @Test
    void testFindRouteById() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(routeEntity));

        RouteDTO result = routeService.findRouteById(routeId);

        assertNotNull(result);
        assertEquals(routeId, result.getId());
        verify(routeRepository).findById(routeId);
    }

    @Test
    void testFindRouteByIdNotFound() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.findRouteById(routeId));
    }

    @Test
    void testFindRoutesByVehicle() {
        when(routeRepository.findByVehicleId(vehicleId)).thenReturn(List.of(routeEntity));

        List<RouteDTO> result = routeService.findRoutesByVehicle(vehicleId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(vehicleId, result.get(0).getVehicleId());
        verify(routeRepository).findByVehicleId(vehicleId);
    }

    @Test
    void testUpdateRoute() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(routeEntity));
        when(routeRepository.save(any(RouteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RouteDTO updatedRouteDTO = routeDTO.toBuilder()
                .estimatedTime("3 hours")
                .trafficConditions("Heavy traffic")
                .build();

        RouteDTO result = routeService.updateRoute(routeId, updatedRouteDTO);

        assertNotNull(result);
        assertEquals("3 hours", result.getEstimatedTime());
        verify(routeRepository).save(any(RouteEntity.class));
    }

    @Test
    void testDeleteRoute() {
        when(routeRepository.existsById(routeId)).thenReturn(true);

        routeService.deleteRoute(routeId);

        verify(routeRepository).deleteById(routeId);
    }

    @Test
    void testDeleteRouteNotFound() {
        when(routeRepository.existsById(routeId)).thenReturn(false);

        assertThrows(RouteNotFoundException.class, () -> routeService.deleteRoute(routeId));
    }
}
