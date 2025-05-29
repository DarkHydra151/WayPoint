package com.logistics.platform.controller;

import com.logistics.platform.domain.RouteDTO;
import com.logistics.platform.exception.RouteNotFoundException;
import com.logistics.platform.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Routes", description = "APIs for managing transportation routes")
public class RouteController {

    private final RouteService routeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all routes", description = "Retrieves all transportation routes in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Routes retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.findAllRoutes());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a route", description = "Allows admins to register a new transportation route.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Route created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid route data")
    })
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routeService.createRoute(routeDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID", description = "Retrieves route details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<RouteDTO> getRouteById(@Parameter(description = "Route ID") @PathVariable UUID id) throws RouteNotFoundException {
        return ResponseEntity.ok(routeService.findRouteById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get routes by vehicle ID", description = "Retrieves all routes assigned to a given vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Routes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<List<RouteDTO>> getRoutesByVehicle(@Parameter(description = "Vehicle ID") @PathVariable UUID vehicleId) {
        return ResponseEntity.ok(routeService.findRoutesByVehicle(vehicleId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Allows admins to update route information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route updated successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<RouteDTO> updateRoute(@Parameter(description = "Route ID") @PathVariable UUID id,
                                                @Valid @RequestBody RouteDTO routeDTO) throws RouteNotFoundException {
        return ResponseEntity.ok(routeService.updateRoute(id, routeDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route", description = "Allows admins to remove a route from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Route deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found")
    })
    public ResponseEntity<Void> deleteRoute(@Parameter(description = "Route ID") @PathVariable UUID id) throws RouteNotFoundException {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
