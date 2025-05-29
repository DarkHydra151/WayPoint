package com.logistics.platform.controller;

import com.logistics.platform.domain.PackageDTO;
import com.logistics.platform.exception.PackageNotFoundException;
import com.logistics.platform.service.PackageService;
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
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Tag(name = "Packages", description = "APIs for managing packages")
public class PackageController {

    private final PackageService packageService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all packages", description = "Retrieves all packages in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<PackageDTO>> getAllPackages() {
        return ResponseEntity.ok(packageService.findAllPackages());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a package", description = "Allows users to add a package to their order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Package created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid package data")
    })
    public ResponseEntity<PackageDTO> createPackage(@Valid @RequestBody PackageDTO packageDTO) {
        return ResponseEntity.ok(packageService.createPackage(packageDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get package by ID", description = "Retrieves package details by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Package retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<PackageDTO> getPackageById(@Parameter(description = "Package ID") @PathVariable UUID id) throws PackageNotFoundException {
        return ResponseEntity.ok(packageService.findPackageById(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get packages by order ID", description = "Retrieves all packages associated with a given order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<List<PackageDTO>> getPackagesByOrder(@Parameter(description = "Order ID") @PathVariable UUID orderId) {
        return ResponseEntity.ok(packageService.findPackagesByOrder(orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update package information", description = "Allows admins to modify package details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Package updated successfully"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<PackageDTO> updatePackage(@Parameter(description = "Package ID") @PathVariable UUID id,
                                                    @Valid @RequestBody PackageDTO packageDTO) throws PackageNotFoundException {
        return ResponseEntity.ok(packageService.updatePackage(id, packageDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete package", description = "Allows admins to remove a package from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Package deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<Void> deletePackage(@Parameter(description = "Package ID") @PathVariable UUID id) throws PackageNotFoundException {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
