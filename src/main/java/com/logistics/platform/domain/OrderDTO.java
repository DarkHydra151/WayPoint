package com.logistics.platform.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class OrderDTO {

    UUID id;

    @NotNull(message = "Client ID must be provided")
    UUID clientId;

    @NotNull(message = "Order status must be provided")
    String status;

    @NotNull(message = "Origin must be specified")
    String origin;

    @NotNull(message = "Destination must be specified")
    String destination;

    LocalDateTime estimatedDeliveryTime;
}
