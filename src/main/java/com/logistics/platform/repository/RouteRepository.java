package com.logistics.platform.repository;

import com.logistics.platform.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, UUID> {
    List<RouteEntity> findByVehicleId(UUID vehicleId);
}
