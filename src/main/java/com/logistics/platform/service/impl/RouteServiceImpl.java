package com.logistics.platform.service.impl;

import com.logistics.platform.domain.RouteDTO;
import com.logistics.platform.entity.RouteEntity;
import com.logistics.platform.entity.VehicleEntity;
import com.logistics.platform.exception.RouteNotFoundException;
import com.logistics.platform.exception.VehicleNotFoundException;
import com.logistics.platform.repository.RouteRepository;
import com.logistics.platform.repository.VehicleRepository;
import com.logistics.platform.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> findAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteDTO createRoute(RouteDTO routeDTO) {
        VehicleEntity vehicle = vehicleRepository.findById(routeDTO.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + routeDTO.getVehicleId()));

        RouteEntity route = RouteEntity.builder()
                .vehicle(vehicle)
                .origin(routeDTO.getOrigin())
                .destination(routeDTO.getDestination())
                .estimatedTime(routeDTO.getEstimatedTime())
                .trafficConditions(routeDTO.getTrafficConditions())
                .build();

        RouteEntity savedRoute = routeRepository.save(route);
        return mapToDTO(savedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDTO findRouteById(UUID id) {
        RouteEntity route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        return mapToDTO(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteDTO> findRoutesByVehicle(UUID vehicleId) {
        return routeRepository.findByVehicleId(vehicleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RouteDTO updateRoute(UUID id, RouteDTO updatedRoute) {
        RouteEntity route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        route = route.toBuilder()
                .origin(updatedRoute.getOrigin())
                .destination(updatedRoute.getDestination())
                .estimatedTime(updatedRoute.getEstimatedTime())
                .trafficConditions(updatedRoute.getTrafficConditions())
                .build();

        RouteEntity savedRoute = routeRepository.save(route);
        return mapToDTO(savedRoute);
    }

    @Override
    @Transactional
    public void deleteRoute(UUID id) {
        if (!routeRepository.existsById(id)) {
            throw new RouteNotFoundException("Route not found with id: " + id);
        }
        routeRepository.deleteById(id);
    }

    private RouteDTO mapToDTO(RouteEntity route) {
        return RouteDTO.builder()
                .id(route.getId())
                .vehicleId(route.getVehicle().getId())
                .origin(route.getOrigin())
                .destination(route.getDestination())
                .estimatedTime(route.getEstimatedTime())
                .trafficConditions(route.getTrafficConditions())
                .build();
    }
}
