package com.logistics.platform.controller;

import com.logistics.platform.domain.VehicleDTO;
import com.logistics.platform.exception.VehicleNotFoundException;
import com.logistics.platform.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "APIs for managing vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Retrieves all registered vehicles in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.findAllVehicles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Register a new vehicle", description = "Allows admins to add a new vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle data")
    })
    public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.createVehicle(vehicleDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves vehicle details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<VehicleDTO> getVehicleById(@Parameter(description = "Vehicle ID") @PathVariable UUID id) throws VehicleNotFoundException {
        return ResponseEntity.ok(vehicleService.findVehicleById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign-driver/{driverId}")
    @Operation(summary = "Assign driver to vehicle", description = "Allows admins to assign a driver to a vehicle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle or driver not found")
    })
    public ResponseEntity<VehicleDTO> assignDriver(@Parameter(description = "Vehicle ID") @PathVariable UUID id,
                                                   @Parameter(description = "Driver ID") @PathVariable UUID driverId) throws VehicleNotFoundException {
        return ResponseEntity.ok(vehicleService.assignDriver(id, driverId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle information", description = "Allows admins to modify vehicle details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<VehicleDTO> updateVehicle(@Parameter(description = "Vehicle ID") @PathVariable UUID id,
                                                    @Valid @RequestBody VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Allows admins to remove a vehicle from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<Void> deleteVehicle(@Parameter(description = "Vehicle ID") @PathVariable UUID id) throws VehicleNotFoundException {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
