package com.logistics.platform.repository;

import com.logistics.platform.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, UUID> {
    List<PackageEntity> findByOrderId(UUID orderId);
}
