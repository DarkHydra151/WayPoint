package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "vehicles")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private UserEntity driver;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    private Float capacity;
    private String currentLocation;
}
