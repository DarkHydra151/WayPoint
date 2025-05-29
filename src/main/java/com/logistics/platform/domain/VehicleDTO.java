package com.logistics.platform.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class VehicleDTO {

    UUID id;

    UUID driverId;

    @NotNull(message = "Vehicle type must be provided")
    String type;

    @NotNull(message = "License plate must be provided")
    String licensePlate;

    Float capacity;

    String currentLocation;
}
