package com.logistics.platform.service.impl;

import com.logistics.platform.domain.PackageDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.entity.PackageEntity;
import com.logistics.platform.exception.OrderNotFoundException;
import com.logistics.platform.exception.PackageNotFoundException;
import com.logistics.platform.repository.OrderRepository;
import com.logistics.platform.repository.PackageRepository;
import com.logistics.platform.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> findAllPackages() {
        return packageRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PackageDTO createPackage(PackageDTO packageDTO) {
        OrderEntity order = orderRepository.findById(packageDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + packageDTO.getOrderId()));

        PackageEntity packageEntity = PackageEntity.builder()
                .order(order)
                .description(packageDTO.getDescription())
                .weight(packageDTO.getWeight())
                .status(packageDTO.getStatus())
                .build();

        PackageEntity savedPackage = packageRepository.save(packageEntity);
        return mapToDTO(savedPackage);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageDTO findPackageById(UUID id) {
        PackageEntity packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with id: " + id));
        return mapToDTO(packageEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> findPackagesByOrder(UUID orderId) {
        return packageRepository.findByOrderId(orderId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PackageDTO updatePackage(UUID id, PackageDTO updatedPackage) {
        PackageEntity packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Package not found with id: " + id));

        packageEntity = packageEntity.toBuilder()
                .description(updatedPackage.getDescription())
                .weight(updatedPackage.getWeight())
                .status(updatedPackage.getStatus())
                .build();

        PackageEntity savedPackage = packageRepository.save(packageEntity);
        return mapToDTO(savedPackage);
    }

    @Override
    @Transactional
    public void deletePackage(UUID id) {
        if (!packageRepository.existsById(id)) {
            throw new PackageNotFoundException("Package not found with id: " + id);
        }
        packageRepository.deleteById(id);
    }

    private PackageDTO mapToDTO(PackageEntity packageEntity) {
        return PackageDTO.builder()
                .id(packageEntity.getId())
                .orderId(packageEntity.getOrder().getId())
                .description(packageEntity.getDescription())
                .weight(packageEntity.getWeight())
                .status(packageEntity.getStatus())
                .build();
    }
}
