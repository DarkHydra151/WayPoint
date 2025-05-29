package com.logistics.platform.service;

import com.logistics.platform.domain.RouteDTO;
import com.logistics.platform.exception.RouteNotFoundException;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    List<RouteDTO> findAllRoutes();
    RouteDTO createRoute(RouteDTO routeDTO);
    RouteDTO findRouteById(UUID id) throws RouteNotFoundException;
    List<RouteDTO> findRoutesByVehicle(UUID vehicleId);
    RouteDTO updateRoute(UUID id, RouteDTO updatedRoute) throws RouteNotFoundException;
    void deleteRoute(UUID id) throws RouteNotFoundException;
}
