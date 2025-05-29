package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "warehouses")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String location;

    private Integer capacity;
    private Integer availableSpace;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private UserEntity manager;
}
