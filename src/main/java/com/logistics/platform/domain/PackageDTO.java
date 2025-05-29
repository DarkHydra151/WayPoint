package com.logistics.platform.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class PackageDTO {

    UUID id;

    @NotNull(message = "Order ID must be provided")
    UUID orderId;

    String description;

    Float weight;

    @NotNull(message = "Package status must be provided")
    String status;
}
