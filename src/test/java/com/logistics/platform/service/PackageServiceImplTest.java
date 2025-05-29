package com.logistics.platform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logistics.platform.domain.PackageDTO;
import com.logistics.platform.entity.OrderEntity;
import com.logistics.platform.entity.PackageEntity;
import com.logistics.platform.exception.OrderNotFoundException;
import com.logistics.platform.exception.PackageNotFoundException;
import com.logistics.platform.repository.OrderRepository;
import com.logistics.platform.repository.PackageRepository;
import com.logistics.platform.service.impl.PackageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PackageServiceImplTest {

    @Mock
    private PackageRepository packageRepository;
    @Mock
    private OrderRepository orderRepository;

    private PackageServiceImpl packageService;

    private UUID packageId, orderId;
    private PackageEntity packageEntity;
    private OrderEntity order;
    private PackageDTO packageDTO;

    @BeforeEach
    void setUp() {
        packageService = new PackageServiceImpl(packageRepository, orderRepository);
        packageId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        order = OrderEntity.builder()
                .id(orderId)
                .status("PENDING")
                .build();

        packageEntity = PackageEntity.builder()
                .id(packageId)
                .order(order)
                .description("Electronics")
                .weight(2.5f)
                .status("PACKED")
                .build();

        packageDTO = PackageDTO.builder()
                .id(packageId)
                .orderId(orderId)
                .description("Electronics")
                .weight(2.5f)
                .status("PACKED")
                .build();
    }

    @Test
    void testFindAllPackages() {
        when(packageRepository.findAll()).thenReturn(List.of(packageEntity));

        List<PackageDTO> result = packageService.findAllPackages();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getDescription());
        verify(packageRepository).findAll();
    }

    @Test
    void testCreatePackage() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(packageRepository.save(any(PackageEntity.class))).thenReturn(packageEntity);

        PackageDTO result = packageService.createPackage(packageDTO);

        assertNotNull(result);
        assertEquals("PACKED", result.getStatus());
        verify(packageRepository).save(any(PackageEntity.class));
    }

    @Test
    void testCreatePackageOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> packageService.createPackage(packageDTO));
    }

    @Test
    void testFindPackageById() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(packageEntity));

        PackageDTO result = packageService.findPackageById(packageId);

        assertNotNull(result);
        assertEquals(packageId, result.getId());
        verify(packageRepository).findById(packageId);
    }

    @Test
    void testFindPackageByIdNotFound() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.empty());

        assertThrows(PackageNotFoundException.class, () -> packageService.findPackageById(packageId));
    }

    @Test
    void testFindPackagesByOrder() {
        when(packageRepository.findByOrderId(orderId)).thenReturn(List.of(packageEntity));

        List<PackageDTO> result = packageService.findPackagesByOrder(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, result.get(0).getOrderId());
        verify(packageRepository).findByOrderId(orderId);
    }

    @Test
    void testUpdatePackage() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(packageEntity));
        when(packageRepository.save(any(PackageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PackageDTO updatedPackageDTO = packageDTO.toBuilder()
                .status("IN_TRANSIT")
                .build();

        PackageDTO result = packageService.updatePackage(packageId, updatedPackageDTO);

        assertNotNull(result);
        assertEquals("IN_TRANSIT", result.getStatus());
        verify(packageRepository).save(any(PackageEntity.class));
    }

    @Test
    void testDeletePackage() {
        when(packageRepository.existsById(packageId)).thenReturn(true);

        packageService.deletePackage(packageId);

        verify(packageRepository).deleteById(packageId);
    }

    @Test
    void testDeletePackageNotFound() {
        when(packageRepository.existsById(packageId)).thenReturn(false);

        assertThrows(PackageNotFoundException.class, () -> packageService.deletePackage(packageId));
    }
}
