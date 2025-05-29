package com.logistics.platform.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class WarehouseDTO {

    UUID id;

    @NotNull(message = "Location must be provided")
    String location;

    Integer capacity;

    Integer availableSpace;

    UUID managerId;
}
