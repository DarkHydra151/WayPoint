package com.logistics.platform.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class RouteDTO {

    UUID id;

    @NotNull(message = "Vehicle ID must be provided")
    UUID vehicleId;

    @NotNull(message = "Origin must be specified")
    String origin;

    @NotNull(message = "Destination must be specified")
    String destination;

    String estimatedTime;

    String trafficConditions;
}
