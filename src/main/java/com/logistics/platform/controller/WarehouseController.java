package com.logistics.platform.controller;

import com.logistics.platform.domain.WarehouseDTO;
import com.logistics.platform.exception.WarehouseNotFoundException;
import com.logistics.platform.service.WarehouseService;
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
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "APIs for managing warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all warehouses", description = "Retrieves all warehouses in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.findAllWarehouses());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new warehouse", description = "Allows admins to register a new warehouse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid warehouse data")
    })
    public ResponseEntity<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouseDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID", description = "Retrieves warehouse details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    public ResponseEntity<WarehouseDTO> getWarehouseById(@Parameter(description = "Warehouse ID") @PathVariable UUID id) throws WarehouseNotFoundException {
        return ResponseEntity.ok(warehouseService.findWarehouseById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/location/{location}")
    @Operation(summary = "Get warehouse by location", description = "Retrieves warehouse details by location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    public ResponseEntity<WarehouseDTO> getWarehouseByLocation(@Parameter(description = "Warehouse Location") @PathVariable String location) throws WarehouseNotFoundException {
        return ResponseEntity.ok(warehouseService.findWarehouseByLocation(location));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update warehouse", description = "Allows admins to update warehouse information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse updated successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    public ResponseEntity<WarehouseDTO> updateWarehouse(@Parameter(description = "Warehouse ID") @PathVariable UUID id,
                                                        @Valid @RequestBody WarehouseDTO warehouseDTO) throws WarehouseNotFoundException {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warehouse", description = "Allows admins to remove a warehouse from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Warehouse deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    public ResponseEntity<Void> deleteWarehouse(@Parameter(description = "Warehouse ID") @PathVariable UUID id) throws WarehouseNotFoundException {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
